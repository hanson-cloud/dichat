package com.diqin.cloud.module.im.convert.user;

import com.diqin.cloud.module.im.controller.app.user.vo.AppImUserRespVO;
import com.diqin.cloud.module.im.dal.dataobject.user.ImUserDO;
import org.mapstruct.factory.Mappers;

public interface ImUserConvert {

    ImUserConvert INSTANCE = Mappers.getMapper(ImUserConvert.class);

    AppImUserRespVO convert(ImUserDO bean);


//    @Mappings({
//            @Mapping(source = "level", target = "level"),
//            @Mapping(source = "bean.id", target = "id"),
//            @Mapping(source = "bean.experience", target = "experience")
//    })
//    AppImUserRespVO convert(ImUserDO bean, MemberLevelDO level);
//
//    MemberUserRespDTO convert2(MemberUserDO bean);
//
//    List<MemberUserRespDTO> convertList2(List<MemberUserDO> list);
//
//    MemberUserDO convert(MemberUserUpdateReqVO bean);
//
//    PageResult<MemberUserRespVO> convertPage(PageResult<MemberUserDO> page);
//
//    @Mapping(source = "areaId", target = "areaName", qualifiedByName = "convertAreaIdToAreaName")
//    MemberUserRespVO convert03(MemberUserDO bean);
//
//    default PageResult<MemberUserRespVO> convertPage(PageResult<MemberUserDO> pageResult,
//                                                     List<MemberTagDO> tags,
//                                                     List<MemberLevelDO> levels,
//                                                     List<MemberGroupDO> groups) {
//        PageResult<MemberUserRespVO> result = convertPage(pageResult);
//        // 处理关联数据
//        Map<Long, String> tagMap = convertMap(tags, MemberTagDO::getId, MemberTagDO::getName);
//        Map<Long, String> levelMap = convertMap(levels, MemberLevelDO::getId, MemberLevelDO::getName);
//        Map<Long, String> groupMap = convertMap(groups, MemberGroupDO::getId, MemberGroupDO::getName);
//        // 填充关联数据
//        result.getList().forEach(user -> {
//            user.setTagNames(convertList(user.getTagIds(), tagMap::get));
//            user.setLevelName(levelMap.get(user.getLevelId()));
//            user.setGroupName(groupMap.get(user.getGroupId()));
//        });
//        return result;
//    }

}
