package com.example.data.mapper.community

import com.example.domain.model.community.Comment
import com.example.domain.model.community.CommunityPost
import com.example.network.dto.community.CommentDto
import com.example.network.dto.community.PostDto

fun PostDto.toCommunityPost(): CommunityPost =
    CommunityPost(
        id = postId,
        author = authorName.orEmpty(),
        avatarUrl = authorAvatarUrl.orEmpty(),
        location = location.orEmpty(),
        timeAgo = createdAt.orEmpty(),
        content = content.orEmpty(),
        imageUrls = imageUrls.orEmpty(),
        likesCount = likesCount,
        commentsCount = commentsCount,
        rating = rating,
        isLiked = isLiked,
        isBookmarked = false,
        comments = comments?.map { it.toComment() }.orEmpty()
    )

fun CommentDto.toComment(): Comment =
    Comment(
        id = id,
        authorName = authorName.orEmpty(),
        avatarUrl = authorAvatarUrl.orEmpty(),
        text = content,
        timeAgo = createdAt.orEmpty()
    )
