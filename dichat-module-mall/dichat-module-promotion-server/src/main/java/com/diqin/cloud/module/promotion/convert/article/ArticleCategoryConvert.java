package com.diqin.cloud.module.promotion.convert.article;

import com.diqin.cloud.framework.common.pojo.PageResult;
import com.diqin.cloud.module.promotion.controller.admin.article.vo.category.ArticleCategoryCreateReqVO;
import com.diqin.cloud.module.promotion.controller.admin.article.vo.category.ArticleCategoryRespVO;
import com.diqin.cloud.module.promotion.controller.admin.article.vo.category.ArticleCategorySimpleRespVO;
import com.diqin.cloud.module.promotion.controller.admin.article.vo.category.ArticleCategoryUpdateReqVO;
import com.diqin.cloud.module.promotion.controller.app.article.vo.category.AppArticleCategoryRespVO;
import com.diqin.cloud.module.promotion.dal.dataobject.article.ArticleCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文章分类 Convert
 *
 * @author hanson
 */
@Mapper
public interface ArticleCategoryConvert {

    ArticleCategoryConvert INSTANCE = Mappers.getMapper(ArticleCategoryConvert.class);

    ArticleCategoryDO convert(ArticleCategoryCreateReqVO bean);

    ArticleCategoryDO convert(ArticleCategoryUpdateReqVO bean);

    ArticleCategoryRespVO convert(ArticleCategoryDO bean);

    List<ArticleCategoryRespVO> convertList(List<ArticleCategoryDO> list);

    PageResult<ArticleCategoryRespVO> convertPage(PageResult<ArticleCategoryDO> page);

    List<ArticleCategorySimpleRespVO> convertList03(List<ArticleCategoryDO> list);

    List<AppArticleCategoryRespVO> convertList04(List<ArticleCategoryDO> categoryList);

}
