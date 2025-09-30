package com.qianshe.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qianshe.notification.entity.NotificationTemplate;
import com.qianshe.notification.enums.NotificationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知模板Mapper接口
 *
 * @author qianshe
 * @since 1.0.0
 */
@Mapper
public interface NotificationTemplateMapper extends BaseMapper<NotificationTemplate> {

    /**
     * 根据模板编码查询模板
     */
    @Select("SELECT * FROM notification_template WHERE template_code = #{templateCode}")
    NotificationTemplate selectByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 根据模板编码和启用状态查询模板
     */
    @Select("SELECT * FROM notification_template WHERE template_code = #{templateCode} AND enabled = #{enabled}")
    NotificationTemplate selectByTemplateCodeAndEnabled(@Param("templateCode") String templateCode, @Param("enabled") Boolean enabled);

    /**
     * 根据通知类型查询启用的模板
     */
    @Select("SELECT * FROM notification_template WHERE type = #{type} AND enabled = #{enabled} ORDER BY created_at DESC")
    List<NotificationTemplate> selectByTypeAndEnabled(@Param("type") String type, @Param("enabled") Boolean enabled);

    /**
     * 根据通知类型分页查询模板
     */
    @Select("SELECT * FROM notification_template WHERE type = #{type} ORDER BY created_at DESC")
    IPage<NotificationTemplate> selectPageByType(Page<NotificationTemplate> page, @Param("type") String type);

    /**
     * 根据启用状态分页查询模板
     */
    @Select("SELECT * FROM notification_template WHERE enabled = #{enabled} ORDER BY created_at DESC")
    IPage<NotificationTemplate> selectPageByEnabled(Page<NotificationTemplate> page, @Param("enabled") Boolean enabled);

    /**
     * 根据创建者查询模板
     */
    @Select("SELECT * FROM notification_template WHERE creator_id = #{creatorId} ORDER BY created_at DESC")
    IPage<NotificationTemplate> selectPageByCreatorId(Page<NotificationTemplate> page, @Param("creatorId") Long creatorId);

    /**
     * 模糊查询模板名称
     */
    @Select("SELECT * FROM notification_template WHERE template_name LIKE CONCAT('%', #{templateName}, '%') ORDER BY created_at DESC")
    IPage<NotificationTemplate> selectPageByTemplateNameLike(Page<NotificationTemplate> page, @Param("templateName") String templateName);

    /**
     * 检查模板编码是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM notification_template WHERE template_code = #{templateCode}")
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 检查模板编码是否存在（排除指定ID）
     */
    @Select("SELECT COUNT(*) > 0 FROM notification_template WHERE template_code = #{templateCode} AND id != #{id}")
    boolean existsByTemplateCodeAndIdNot(@Param("templateCode") String templateCode, @Param("id") Long id);

    /**
     * 查询支持指定渠道的模板
     */
    @Select("SELECT * FROM notification_template WHERE enabled = true AND supported_channels LIKE CONCAT('%', #{channel}, '%')")
    List<NotificationTemplate> selectBySupportedChannelsContaining(@Param("channel") String channel);

    /**
     * 根据类型和支持的渠道查询模板
     */
    @Select("SELECT * FROM notification_template WHERE type = #{type} AND enabled = true AND supported_channels LIKE CONCAT('%', #{channel}, '%')")
    List<NotificationTemplate> selectByTypeAndSupportedChannelsContaining(@Param("type") String type, @Param("channel") String channel);

    /**
     * 统计各类型的模板数量
     */
    @Select("SELECT type, COUNT(*) as count FROM notification_template WHERE enabled = true GROUP BY type")
    List<Object[]> countTemplatesByType();

    /**
     * 统计启用和禁用的模板数量
     */
    @Select("SELECT enabled, COUNT(*) as count FROM notification_template GROUP BY enabled")
    List<Object[]> countTemplatesByEnabled();
}