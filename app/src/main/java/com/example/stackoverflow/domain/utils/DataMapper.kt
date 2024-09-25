package com.example.stackoverflow.domain.utils

import com.example.stackoverflow.data.network.model.AnswerResponse
import com.example.stackoverflow.data.network.model.Owner
import com.example.stackoverflow.data.network.model.QuestionResponse
import com.example.stackoverflow.data.room.entity.AnswerEntity
import com.example.stackoverflow.data.room.entity.OwnerEntity
import com.example.stackoverflow.data.room.entity.QuestionEntity

object DataMapper {
    fun toEntity(model: QuestionResponse): QuestionEntity {
        return QuestionEntity(
            model.id,
            model.title,
            model.questionLink,
            model.tags,
            toEntity(model.owner)
        )
    }

    fun toEntity(model: AnswerResponse): AnswerEntity {
        return AnswerEntity(
            model.answerId,
            model.questionId,
            model.isAccepted,
            model.body,
            model.score,
            toEntity(model.owner)
        )
    }

    private fun toEntity(model: Owner): OwnerEntity {
        return OwnerEntity(
            model.accountId,
            model.userId,
            model.profileImageLink,
            model.name,
            model.profileLink
        )
    }
}