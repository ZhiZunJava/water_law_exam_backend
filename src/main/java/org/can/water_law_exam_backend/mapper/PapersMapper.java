package org.can.water_law_exam_backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.can.water_law_exam_backend.entity.Papers;

import java.util.List;

@Mapper
public interface PapersMapper {
    int insert(Papers entity);
    int deleteBatch(@Param("ids") List<Long> ids);
    Papers selectById(@Param("id") Long id);
    List<Papers> selectByPage(@Param("key") String key);
    List<Papers> selectByGroupId(@Param("groupId") Long groupId);
    List<Papers> selectByGroupIds(@Param("groupIds") List<Long> groupIds);
    Papers selectByGroupAndNo(@Param("groupId") Long groupId,
                              @Param("papersNo") Integer papersNo);
}
