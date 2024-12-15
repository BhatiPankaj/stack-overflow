package com.example.stackoverflow.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stackoverflow.data.room.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Query("Select * from remote_keys where id = 0")
    suspend fun getRemoteKey() : RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKey(remoteKeyEntity: RemoteKeyEntity)

    @Query("Delete from remote_keys where id = 0")
    suspend fun clearRemoteKeys()
}