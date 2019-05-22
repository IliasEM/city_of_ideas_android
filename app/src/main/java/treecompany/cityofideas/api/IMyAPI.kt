package treecompany.cityofideas.api

import io.reactivex.Observable
import retrofit2.http.*
import treecompany.cityofideas.responses.*

interface IMyAPI {

    //Account
    @POST("api/account/register")
    fun register(
        @Query("username") username: String,
        @Query("email") email: String,
        @Query("phonenumber") phonenumber: String,
        @Query("street") street: String,
        @Query("city") city: String,
        @Query("post") post: String,
        @Query("password") password: String
    ) : Observable<RegisterResponse>

    @POST("api/account/login")
    fun login(
        @Query("name") name: String,
        @Query("password") password: String
    ) : Observable<LoginResponse>


    @GET("api/account/user")
    fun getUser(
        @Query("userId") userId: String
    ) : Observable<ProfileResponse>


    //Profile
    @POST("api/account/update")
    fun updateUser(
        @Query("userId") userId: String,
        @Query("addressId") addressId: String,
        @Query("phonenumber") phonenumber: String,
        @Query("street") street: String,
        @Query("city") city: String,
        @Query("post") post: String
    ) : Observable<ProfileResponse>

    @POST("api/account/changepassword")
    fun changePassword(
        @Query("userId") userId: String,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String
    ) : Observable<ProfileResponse>


    //Platform
    @GET("api/platform/allplatforms")
    fun getAllPlatforms() : Observable<PlarformsResponse>

    //Project
    @GET("api/project/allprojects")
    fun getAllProjects(
        @Query("platformId") platformId: String,
        @Query("searchString") searchString: String
    ) : Observable<ProjectsResponse>

    @GET("api/project/project")
    fun getProject(
        @Query("id") id: String
    ) : Observable<ProjectResponse>

    @GET("api/project/popularprojects")
    fun getPopularProjects(
        @Query("platformId") platformId: String
    ) : Observable<ProjectsResponse>

    @GET("api/project/recentprojects")
    fun getRecentProjects(
        @Query("platformId") platformId: String
    ) : Observable<ProjectsResponse>


    //Ideation
   @GET("api/ideation/ideation")
   fun getIdeation(
        @Query("id") id: String
    ) : Observable<PhaseIdeationResponse>

    //Idea
    @GET("api/ideation/ideas")
    fun getIdeationIdeas(
        @Query("ideationId") ideationId: String
    ) : Observable<IdeasResponse>

    @GET("api/ideation/idea")
    fun getIdea(
        @Query("id") id: String
    ) : Observable<IdeaResponse>

    @POST("api/ideation/idea")
    fun postIdea(
        @Query("ideationId") ideationId: String,
        @Query("title") title: String,
        @Query("description") description: String,
        @Query("image") image: String,
        @Query("userId") userId: String
    ) : Observable<DefaultResponse>


    //Comment
    @POST("api/ideation/comment")
    fun postComment(
        @Query("ideaId") ideaId: String,
        @Query("value") value: String,
        @Query("userId") userId: String
    ) : Observable<CommentAddResponse>


    //Reply
    @POST("api/ideation/reply")
    fun postReply(
        @Query("commentId") commentId: String,
        @Query("value") value: String,
        @Query("userId") userId: String
    ) : Observable<RepliesResponse>

    @GET("api/ideation/reply")
    fun getReplies(
        @Query("commentId") commentId: String
    ) : Observable<IdeaResponse>

    //Report comment and reply
    @POST("api/ideation/reportComment")
    fun reportComment(
        @Query("commentId") commentId: String,
        @Query("userId") userId: String
    ) : Observable<DefaultResponse>

    @POST("api/ideation/reportReply")
    fun reportReply(
        @Query("replyId") replyId: String,
        @Query("userId") userId: String
    ) : Observable<DefaultResponse>


    //Vote on project
    @POST("api/project/vote")
    fun voteOnProject(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @POST("api/project/voteRemove")
    fun revokeVoteOnProject(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @GET("api/project/voteCheck")
    fun checkUserVoted(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>


    //Like an project
    @POST("api/project/like")
    fun likeOnProject(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @POST("api/project/likeRemove")
    fun revokeLikeOnProject(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @GET("api/project/likeCheck")
    fun checkUserLiked(
        @Query("projectId") projectId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>


    //Like an idea
    @POST("api/ideation/idea/like")
    fun likeOnIdea(
        @Query("ideaId") ideaId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @POST("api/ideation/idea/likeRemove")
    fun revokeLikeOnIdea(
        @Query("ideaId") ideaId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

    @GET("api/ideation/idea/likeCheck")
    fun checkUserLikedIdea(
        @Query("ideaId") ideaId: String,
        @Query("userId") userId: String
    ) : Observable<VoteLikeResponse>

}