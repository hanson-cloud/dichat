package com.diqin.cloud.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Schema(description = "分页参数")
@Data
public class PageParam implements Serializable {

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    /**
     * 每页条数 - 不分页
     * <p>
     * 例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    @Schema(description = "页码，从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    @Schema(description = "每页条数，最大值为 200", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数最小值为 1")
    @Max(value = 200, message = "每页条数最大值为 200")
    private Integer pageSize = PAGE_SIZE;

    /**
     * PageParam类的无参构造方法
     * 当创建PageParam对象时不传递任何参数时会调用此构造方法
     */
    public PageParam() {

    }

    /**
     * 分页参数构造函数
     *
     * @param pNo   页码编号，从1开始计数
     * @param pSize 每页显示的记录数量
     */
    public PageParam(int pNo, int pSize) {
        // 初始化页码
        this.pageNo = pNo;
        // 初始化每页大小
        this.pageSize = pSize;
    }
}
