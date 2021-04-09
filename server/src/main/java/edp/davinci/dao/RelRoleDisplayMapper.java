/*
 * <<
 *  Davinci
 *  ==
 *  Copyright (C) 2016 - 2019 EDP
 *  ==
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  >>
 *
 */

package edp.davinci.dao;

import edp.davinci.core.model.RoleDisableViz;
import edp.davinci.model.RelRoleDisplay;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RelRoleDisplayMapper {
    int insert(RelRoleDisplay record);

    void insertBatch(List<RelRoleDisplay> list);

    @Delete({
            "delete from rel_role_display where display_id = #{id}"
    })
    int deleteByDisplayId(Long id);

//  17231
    @Select({
            "select ru.role_id roleId,d.id vizId",
            "from display d",
            "INNER JOIN rel_role_user ru ON ru.user_id = #{userId} AND d.project_id = #{projectId}",
            "left join rel_role_display rd ON ru.role_id = rd.role_id AND rd.display_id = d.id AND rd.visible =1",
            "WHERE rd.role_id IS NULL "
    })
    List<RoleDisableViz> getDisableDisplayByUser(@Param("userId") Long userId, @Param("projectId") Long projectId);
    @Select({
            "select role_id from rel_role_display where display_id = #{display_id} and visible = 0"
    })
    List<Long> getById(Long displayId);
//17231
    @Select({
            "select d.id display_id",
            "from display d",
            "LEFT JOIN rel_role_display rd on d.id = rd.display_id AND rd.role_id = #{id} AND d.project_id = #{projectId}"
    })
    List<Long> getExcludeDisplays(@Param("id") Long id, @Param("projectId") Long projectId);

    @Delete({"delete from rel_role_display where display_id = #{displayId} and role_id = #{roleId}"})
    int delete(@Param("displayId") Long displayId, @Param("roleId") Long roleId);

    @Delete({"delete from rel_role_display where role_id = #{roleId}"})
    int deleteByRoleId(Long roleId);

    @Insert({
            "insert rel_role_display (role_id, display_id, visible, create_by, create_time)",
            "select role_id, ${copyDisplayId}, visible, ${userId}, now() from rel_role_display where display_id = #{originDisplayId}"
    })
    int copyRoleRelation(@Param("originDisplayId") Long originDisplayId, @Param("copyDisplayId") Long copyDisplayId, @Param("userId") Long userId);

    @Delete({"delete from rel_role_display where display_id in (select id from display where project_id = #{projectId})"})
    int deleteByProject(Long projectId);

    @Delete({"delete from rel_role_display where role_id = #{roleId} and display_id in (select id from display where project_id = #{projectId})"})
    int deleteByRoleAndProject(Long roleId, Long projectId);
}