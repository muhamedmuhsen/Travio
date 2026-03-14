package com.example.data.mapper.community

import com.example.domain.model.community.Comment
import com.example.domain.model.community.CommunityPost
import com.example.network.dto.community.CommentDto
import com.example.network.dto.community.PostDto

fun PostDto.toCommunityPost(): CommunityPost =
    CommunityPost(
        id = this.postId,
        author = this.authorName,
        avatarUrl = this.authorAvatarUrl,
        location = this.location,
        timeAgo = this.createdAt,
        content = this.content,
        imageUrls = this.imageUrls,
        likesCount = this.likesCount,
        commentsCount = this.commentsCount,
        rating = 0.0f,
        isLiked = this.isLiked,
        isBookmarked = false
    )

fun CommentDto.toComment(): Comment =
    Comment(
        id = id,
        authorName = authorName.orEmpty(),
        avatarUrl = authorAvatarUrl.orEmpty(),
        text = content,
        timeAgo = createdAt.orEmpty()
    )
