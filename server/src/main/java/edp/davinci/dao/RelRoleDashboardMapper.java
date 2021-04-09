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
import edp.davinci.model.RelRoleDashboard;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface RelRoleDashboardMapper {

    int insert(RelRoleDashboard relRoleDashboard);

    int insertBatch(List<RelRoleDashboard> list);

//    @Select({
//            "select rru.role_id as roleId, rrd.dashboard_id as vizId",
//            "from rel_role_dashboard rrd",
//            "   inner join rel_role_user rru on rru.role_id = rrd.role_id",
//            "   inner join dashboard d on d.id  = rrd.dashboard_id",
//            "where rru.user_id = #{userId} and rrd.visible = 0 and d.dashboard_portal_id = #{portalId}"
//    })
//    List<RoleDisableViz> getDisableByUser(@Param("userId") Long userId, @Param("portalId") Long portalId);

    //    17231
    @Select({
            " select ru.role_id roleId,d.id vizId" +
            " from dashboard d" +
            " INNER JOIN rel_role_user ru ON ru.user_id = #{userId} AND d.dashboard_portal_id = #{portalId}" +
            " left join rel_role_dashboard rd ON ru.role_id = rd.role_id AND rd.dashboard_id = d.id AND rd.visible =1" +
            " WHERE rd.role_id IS NULL "
    })
    List<RoleDisableViz> getDisableByUser(@Param("userId") Long userId, @Param("portalId") Long portalId);

    @Select("select role_id from rel_role_dashboard where dashboard_id = #{dashboardId} and visible = 0")
    List<Long> getExcludeRoles(@Param("dashboardId") Long dashboardId);

    int deleteByDashboardIds(@Param("dashboardIds") Set<Long> dashboardIds);

    @Delete({
            "delete from rel_role_dashboard where dashboard_id in (select id from dashboard where id = #{id} or find_in_set(#{id}, full_parent_Id) > 0)"
    })
    int deleteByDashboardId(Long id);

//    17231
    @Select({
            "select d.id dashboard_id",
            "from dashboard d",
            "INNER JOIN dashboard_portal dp on dp.id = d.dashboard_portal_id",
            "left join rel_role_dashboard rd on d.id = rd.dashboard_id and rd.role_id = #{id}  and rd.visible = 1",
            "where dp.project_id = #{projectId}",
            "AND rd.role_id IS NULL"
    })
    List<Long> getExcludeDashboards(@Param("id") Long id, @Param("projectId") Long projectId);

    @Delete({"delete from rel_role_dashboard where dashboard_id = #{dashboardId} and role_id = #{roleId}"})
    int delete(@Param("dashboardId") Long dashboardId, @Param("roleId") Long roleId);

    @Delete({"delete from rel_role_dashboard where role_id = #{roleId}"})
    int deleteByRoleId(Long roleId);

    @Delete({"DELETE rrd FROM rel_role_dashboard rrd WHERE rrd.dashboard_id IN " +
            "( " +
            "SELECT d.id " +
            "FROM dashboard d " +
            "WHERE d.dashboard_portal_id = #{portalId} " +
            ") "})
    int deleteByPortalId(@Param("portalId") Long portalId);

    @Delete({
            "delete from rel_role_dashboard where dashboard_id in (",
            "select d.id from dashboard d left join dashboard_portal p on p.id = d.dashboard_portal_id ",
            "where p.project_id = #{projectId})"
    })
    int deleteByProject(Long projectId);

    @Delete({
        "delete from rel_role_dashboard where role_id = #{roleId} and dashboard_id in (",
        "select d.id from dashboard d left join dashboard_portal p on p.id = d.dashboard_portal_id ",
        "where p.project_id = #{projectId})"
    })
    int deleteByRoleAndProject(@Param("roleId") Long roleId, @Param("projectId") Long projectId);
}