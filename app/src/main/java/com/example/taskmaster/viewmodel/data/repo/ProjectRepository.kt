package com.example.taskmaster.viewmodel.data.repo

import com.example.taskmaster.viewmodel.data.net.ApiFactory
import com.example.taskmaster.viewmodel.data.net.ProjectsApi
import com.example.taskmaster.viewmodel.data.projects.ProjectCodeRequest
import com.example.taskmaster.viewmodel.data.projects.ProjectCreateRequest
import com.example.taskmaster.viewmodel.data.projects.ProjectDto
import com.example.taskmaster.viewmodel.data.projects.ProjectUpdateRequest

class ProjectRepository(
    private val api: ProjectsApi = ApiFactory.projects
) {
    // GET /api/v1/projects
    suspend fun getAll(): List<ProjectDto> = api.getProjects()

    // POST /api/v1/projects
    suspend fun create(body: ProjectCreateRequest): ProjectDto =
        api.createProject(body)

    // GET /api/v1/projects/{projectId}
    suspend fun getById(projectId: Long): ProjectDto =
        api.getProject(projectId)

    // PUT /api/v1/projects/{id}
    suspend fun update(id: Long, body: ProjectUpdateRequest): ProjectDto =
        api.updateProject(id, body)

    // DELETE /api/v1/projects/{id}
    suspend fun delete(id: Long) {
        api.deleteProject(id)
    }

    // PUT /api/v1/projects/{projectId}/code
    suspend fun setCode(projectId: Long, code: String): ProjectDto =
        api.setProjectCode(projectId, ProjectCodeRequest(code))

    // GET /api/v1/projects/member?memberId={memberId}
    suspend fun getByMember(memberId: Long): List<ProjectDto> =
        api.getProjectsByMember(memberId)

    // GET /api/v1/projects/leader?leaderId={leaderId}
    suspend fun getByLeader(leaderId: Long): List<ProjectDto> =
        api.getProjectsByLeader(leaderId)

    // GET /api/v1/projects/join/{key}
    suspend fun joinByKey(key: String): ProjectDto =
        api.joinProjectByKey(key)

    // DELETE /api/v1/projects/{projectId}/members/{memberId}
    suspend fun removeMember(projectId: Long, memberId: Long) {
        api.removeMember(projectId, memberId)
    }
}
