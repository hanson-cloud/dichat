package com.diqin.cloud.module.im.controller.app.bank;

import com.diqin.cloud.framework.common.pojo.CommonResult;
import com.diqin.cloud.framework.common.util.object.BeanUtils;
import com.diqin.cloud.module.im.controller.admin.bank.vo.ImBankRespVO;
import com.diqin.cloud.module.im.dal.dataobject.bank.ImBankDO;
import com.diqin.cloud.module.im.service.bank.ImBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.diqin.cloud.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - IM 支持的银行
 *
 * @author dichat
 */
@Tag(name = "用户 APP - IM 支持的银行")
@RestController
@RequestMapping("/im/bank")
public class AppImBankController {

    @Resource
    private ImBankService bankService;

    @GetMapping("/list")
    @Operation(summary = "获得启用的银行列表（绑卡选择用）")
    public CommonResult<List<ImBankRespVO>> list() {
        List<ImBankDO> list = bankService.getEnabledBanks();
        return success(BeanUtils.toBean(list, ImBankRespVO.class));
    }

}
