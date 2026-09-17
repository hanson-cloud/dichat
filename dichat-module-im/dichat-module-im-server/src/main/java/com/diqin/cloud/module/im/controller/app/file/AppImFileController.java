package com.diqin.cloud.module.im.controller.app.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.diqin.cloud.framework.common.exception.ServiceException;
import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.module.im.config.ImFileProperties;
import com.diqin.cloud.module.im.controller.app.file.vo.AppImFileUploadReqVO;
import com.diqin.cloud.module.im.controller.app.file.vo.AppImImageUploadRespVO;
import com.diqin.cloud.module.im.controller.app.file.vo.AppImVideoUploadRespVO;
import com.diqin.cloud.module.im.enums.ErrorCodeConstants;
import com.diqin.cloud.module.infra.api.file.FileApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * App - IM 文件上传
 *
 * <p>参考 member 模块的路径规范，使用 {@code /im/file} 前缀。
 * <br>网关路由 /app-api/im/** 通过 RewritePath 去掉 /app-api 前缀后转发到 /im/file/upload 等。
 * <br>完整访问路径为 /app-api/im/file/upload 等，与 member 模块的 /app-api/member/xxx 风格一致。
 *
 * @author hanson
 */
@Tag(name = "用户 APP - IM 文件")
@RestController
@RequestMapping("/im/file")
@Validated
@Slf4j
public class AppImFileController {

    /**
     * IM 文件统一目录前缀
     */
    private static final String IM_FILE_DIR_PREFIX = "im";

    @Resource
    private FileApi fileApi;

    @Resource
    private ImFileProperties fileProperties;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件（消息附件 / 头像 / 表情等）")
    public CommonResult<String> uploadFile(@Valid AppImFileUploadReqVO uploadReqVO) throws IOException {
        // 1. 校验文件合法性
        MultipartFile file = uploadReqVO.getFile();
        validateNotEmpty(file);
        // 2. 读取并上传
        byte[] content = IoUtil.readBytes(file.getInputStream());
        String directory = buildImDirectory(uploadReqVO.getDirectory());
        String url = fileApi.createFile(content, file.getOriginalFilename(), directory, file.getContentType());
        log.info("[uploadFile][上传成功][filename={}][size={}][directory={}]", file.getOriginalFilename(), file.getSize(), directory);
        return success(url);
    }

    @PostMapping("/image/upload")
    @Operation(summary = "上传图片", description = "上传图片后返回原图和缩略图的 url")
    @Parameters({
            @Parameter(name = "file", description = "图片文件", required = true),
            @Parameter(name = "thumbSize", description = "缩略图最大边长（像素），默认取 dichat.im.file.default-thumb-size=200", example = "200")
    })
    public CommonResult<AppImImageUploadRespVO> uploadImage(@RequestParam("file") MultipartFile file,
                                                             @RequestParam(value = "thumbSize", required = false) Integer thumbSize) throws IOException {
        // 1. 校验图片合法性
        validateImage(file);
        // 2. 读取原图并上传
        byte[] originalContent = IoUtil.readBytes(file.getInputStream());
        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String directory = buildImDirectory("image");
        String originalUrl = fileApi.createFile(originalContent, originalName, directory, contentType);

        // 3. 生成缩略图（解码失败 / 原图已小于目标尺寸时回退为原图 url）
        String thumbUrl = originalUrl;
        try {
            int targetSize = thumbSize == null ? fileProperties.getDefaultThumbSize() : Math.min(thumbSize, fileProperties.getMaxThumbSize());
            byte[] thumbContent = generateThumbnail(originalContent, targetSize, contentType);
            if (thumbContent != null) {
                String thumbName = buildThumbName(originalName, contentType);
                thumbUrl = fileApi.createFile(thumbContent, thumbName, buildImDirectory("image/thumb"), contentType);
            }
        } catch (Exception e) {
            log.warn("[uploadImage][生成缩略图失败，回退为原图 url][filename={}]", originalName, e);
        }

        // 4. 组装响应
        log.info("[uploadImage][上传成功][filename={}][size={}][thumbSize={}]", originalName, file.getSize(), thumbSize);
        return success(new AppImImageUploadRespVO()
                .setUrl(originalUrl)
                .setThumbUrl(thumbUrl)
                .setName(originalName)
                .setSize(file.getSize())
                .setContentType(contentType));
    }

    @PostMapping("/video/upload")
    @Operation(summary = "上传视频", description = "上传视频并由服务端抽取首帧生成封面，返回视频地址与封面地址（封面生成失败则 thumbnail 为 null，不影响视频上传）。" +
            "可选 thumbnail 参数：客户端（APP 端 chooseMedia/chooseVideo 已能拿到首帧缩略图）以 base64 形式上传的封面，" +
            "当服务端 FFmpeg 抽帧失败（环境缺 ffmpeg）时回退使用，彻底去掉对 FFmpeg 的硬依赖。")
    public CommonResult<AppImVideoUploadRespVO> uploadVideo(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "thumbnail", required = false) String thumbnail) throws IOException {
        // 1. 校验视频合法性
        validateVideo(file);
        // 2. 读取并上传原视频
        byte[] videoContent = IoUtil.readBytes(file.getInputStream());
        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();
        String directory = buildImDirectory("video");
        String videoUrl = fileApi.createFile(videoContent, originalName, directory, contentType);

        AppImVideoUploadRespVO resp = new AppImVideoUploadRespVO()
                .setUrl(videoUrl)
                .setName(originalName)
                .setSize(file.getSize())
                .setContentType(contentType);

        // 3. 抽封面 + 取元数据（失败兜底，不阻断视频上传）
        java.io.File tmpVideo = null;
        try {
            tmpVideo = java.io.File.createTempFile("im_video_", ".tmp");
            java.nio.file.Files.write(tmpVideo.toPath(), videoContent);
            // 3.1 抽首帧封面：优先服务端 FFmpeg；FFmpeg 不可用（环境缺 ffmpeg）且客户端传了封面时，回退用客户端封面
            int coverWidth = fileProperties.getDefaultVideoCoverWidth() == null ? 480 : fileProperties.getDefaultVideoCoverWidth();
            String coverUrl = generateVideoCover(tmpVideo, coverWidth);
            if (coverUrl == null && StrUtil.isNotBlank(thumbnail)) {
                coverUrl = storeClientThumbnail(thumbnail);
            }
            resp.setThumbnail(coverUrl); // 失败为 null
            // 3.2 ffprobe 取真实宽高 / 时长（可选）
            extractVideoMeta(tmpVideo, resp);
        } catch (Exception e) {
            log.warn("[uploadVideo][抽封面/取元数据失败，回退无封面][filename={}]", originalName, e);
        } finally {
            if (tmpVideo != null && tmpVideo.exists()) {
                boolean deleted = tmpVideo.delete();
                if (!deleted) {
                    tmpVideo.deleteOnExit();
                }
            }
        }

        log.info("[uploadVideo][上传成功][filename={}][size={}][hasCover={}]", originalName, file.getSize(), resp.getThumbnail() != null);
        return success(resp);
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "对私有文件 URL 进行临时签名，前端可用来直接访问")
    @Parameter(name = "url", description = "原始文件 URL", required = true)
    @Parameter(name = "expirationSeconds", description = "签名有效期（秒）", example = "1800")
    public CommonResult<String> getPresignedUrl(@RequestParam("url") String url,
                                                   @RequestParam(value = "expirationSeconds", required = false) Integer expirationSeconds) {
        return success(fileApi.presignGetUrl(url, expirationSeconds).getCheckedData());
    }

    // ==================== 私有方法 ====================

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ErrorCodeConstants.FILE_NOT_EMPTY);
        }
        if (file.getSize() > fileProperties.getMaxFileSize()) {
            throw new ServiceException(ErrorCodeConstants.FILE_SIZE_EXCEEDED);
        }
    }

    private void validateImage(MultipartFile file) {
        validateNotEmpty(file);
        String contentType = file.getContentType();
        if (StrUtil.isBlank(contentType) || !contentType.startsWith("image/")) {
            throw new ServiceException(ErrorCodeConstants.FILE_NOT_IMAGE);
        }
    }

    private void validateVideo(MultipartFile file) {
        validateNotEmpty(file);
        String contentType = file.getContentType();
        if (StrUtil.isBlank(contentType) || !contentType.startsWith("video/")) {
            throw new ServiceException(ErrorCodeConstants.FILE_NOT_VIDEO);
        }
    }

    /**
     * 按最大边长等比缩放生成缩略图字节；解码失败、原图已小于目标尺寸或无对应图片写入器时返回 null（调用方回退为原图）
     */
    private byte[] generateThumbnail(byte[] originalContent, int maxSide, String contentType) throws IOException {
        if (maxSide <= 0) {
            return null;
        }
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalContent));
        if (original == null) {
            return null;
        }
        int width = original.getWidth();
        int height = original.getHeight();
        if (width <= maxSide && height <= maxSide) {
            return null;
        }
        double scale = (double) maxSide / Math.max(width, height);
        int thumbWidth = (int) Math.round(width * scale);
        int thumbHeight = (int) Math.round(height * scale);

        // PNG 保留透明度，其余转 JPEG 以减小体积
        boolean isPng = contentType != null && contentType.contains("png");
        String format = isPng ? "png" : "jpg";

        BufferedImage thumbnail = new BufferedImage(thumbWidth, thumbHeight,
                isPng ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isPng) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, thumbWidth, thumbHeight);
            }
            g.drawImage(original, 0, 0, thumbWidth, thumbHeight, null);
        } finally {
            g.dispose();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (!ImageIO.write(thumbnail, format, bos)) {
            return null;
        }
        return bos.toByteArray();
    }

    /**
     * 用 FFmpeg 抽取视频首帧生成封面图并上传，返回封面 URL；抽帧失败 / ffmpeg 不可用 / 封面为空时返回 null。
     * <p>
     * 命令：ffmpeg -ss 0.1 -i in -vframes 1 -vf "scale='min(maxW,iw)':-2" -q:v 3 out.jpg
     * -ss 0.1 跳过全黑首帧；scale 限宽 maxW、高度自适应（-2 保证偶数，编码器友好）；-q:v 3 质量清晰且体积小。
     */
    private String generateVideoCover(java.io.File videoFile, int maxWidth) {
        java.io.File cover = null;
        try {
            cover = java.io.File.createTempFile("im_cover_", ".jpg");
            String ffmpeg = StrUtil.isBlank(fileProperties.getFfmpegPath()) ? "ffmpeg" : fileProperties.getFfmpegPath();
            ProcessBuilder pb = new ProcessBuilder(
                    ffmpeg, "-y", "-ss", "0.1", "-i", videoFile.getAbsolutePath(),
                    "-vframes", "1", "-vf", "scale='min(" + maxWidth + ",iw)':-2", "-q:v", "3",
                    cover.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            // 消费标准输出，避免子进程因管道满而阻塞
            IoUtil.readBytes(p.getInputStream());
            boolean finished = p.waitFor(60, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished || p.exitValue() != 0 || !cover.exists() || cover.length() == 0) {
                return null;
            }
            byte[] coverBytes = java.nio.file.Files.readAllBytes(cover.toPath());
            String coverName = buildCoverName(videoFile.getName());
            return fileApi.createFile(coverBytes, coverName, buildImDirectory("video/cover"), "image/jpeg");
        } catch (Exception e) {
            log.warn("[generateVideoCover][抽帧失败][file={}]", videoFile.getName(), e);
            return null;
        } finally {
            if (cover != null && cover.exists()) {
                boolean deleted = cover.delete();
                if (!deleted) {
                    cover.deleteOnExit();
                }
            }
        }
    }

    /**
     * 存储客户端（APP 端）上传的 base64 封面图，返回封面 URL；解析/存储失败返回 null。
     * <p>用于服务端 FFmpeg 抽帧不可用时的兜底：chooseMedia/chooseVideo 在 APP 端原生就能拿到首帧缩略图，
     * 以 base64（可带 {@code data:image/jpeg;base64,} 前缀）随视频一并上传，避免缺 ffmpeg 时封面缺失。</p>
     */
    private String storeClientThumbnail(String base64) {
        try {
            String pure = base64;
            int comma = base64.indexOf(',');
            if (comma >= 0) {
                pure = base64.substring(comma + 1);
            }
            byte[] bytes = Base64.getDecoder().decode(pure);
            if (bytes.length == 0) {
                return null;
            }
            String name = "client_cover_" + System.nanoTime() + ".jpg";
            return fileApi.createFile(bytes, name, buildImDirectory("video/cover"), "image/jpeg");
        } catch (Exception e) {
            log.warn("[storeClientThumbnail][客户端封面存储失败]", e);
            return null;
        }
    }

    /**
     * 用 ffprobe 抽取视频真实宽高与时长（毫秒），失败不影响封面与视频上传。
     */
    private void extractVideoMeta(java.io.File videoFile, AppImVideoUploadRespVO resp) {
        try {
            String ffprobe = StrUtil.isBlank(fileProperties.getFfprobePath()) ? "ffprobe" : fileProperties.getFfprobePath();
            ProcessBuilder pb = new ProcessBuilder(
                    ffprobe, "-v", "error",
                    "-show_entries", "stream=width,height:format=duration",
                    "-of", "default=noprint_wrappers=1", videoFile.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String out = new String(IoUtil.readBytes(p.getInputStream()), java.nio.charset.StandardCharsets.UTF_8);
            boolean finished = p.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
            if (!finished || p.exitValue() != 0) {
                return;
            }
            Integer width = null;
            Integer height = null;
            Long duration = null;
            for (String line : out.split("\\R")) {
                line = line.trim();
                if (line.startsWith("width=")) {
                    width = parseInteger(line.substring(6));
                } else if (line.startsWith("height=")) {
                    height = parseInteger(line.substring(7));
                } else if (line.startsWith("duration=")) {
                    try {
                        duration = Math.round(Double.parseDouble(line.substring(9).trim()) * 1000);
                    } catch (NumberFormatException ignore) { /* ignore */ }
                }
            }
            if (width != null && width > 0) resp.setWidth(width);
            if (height != null && height > 0) resp.setHeight(height);
            if (duration != null && duration > 0) resp.setDuration(duration);
        } catch (Exception e) {
            log.warn("[extractVideoMeta][取元数据失败][file={}]", videoFile.getName(), e);
        }
    }

    private Integer parseInteger(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String buildCoverName(String originalName) {
        String base = StrUtil.isBlank(originalName) ? "video" : originalName;
        int dotIndex = base.lastIndexOf('.');
        String name = dotIndex > 0 ? base.substring(0, dotIndex) : base;
        return name + "_cover.jpg";
    }

    private String buildThumbName(String originalName, String contentType) {
        String base = StrUtil.isBlank(originalName) ? "image" : originalName;
        int dotIndex = base.lastIndexOf('.');
        String name = dotIndex > 0 ? base.substring(0, dotIndex) : base;
        boolean isPng = contentType != null && contentType.contains("png");
        return name + "_thumb." + (isPng ? "png" : "jpg");
    }

    private String buildImDirectory(String subDirectory) {
        if (subDirectory == null || subDirectory.isBlank()) {
            return IM_FILE_DIR_PREFIX;
        }
        if (subDirectory.equals(IM_FILE_DIR_PREFIX) || subDirectory.startsWith(IM_FILE_DIR_PREFIX + "/")) {
            return subDirectory;
        }
        return IM_FILE_DIR_PREFIX + "/" + subDirectory;
    }
}
