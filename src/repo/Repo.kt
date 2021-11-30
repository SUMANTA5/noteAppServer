package com.example.repo

import com.example.data.model.Note
import com.example.data.model.User
import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.example.repo.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class Repo {

    suspend fun addUser(user: User) {
        dbQuery {
            UserTable.insert { ut ->
                ut[UserTable.email] = user.email
                ut[UserTable.hashPassword] = user.hashPassword
                ut[UserTable.name] = user.userName
            }

        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select {
            UserTable.email.eq(email)
        }
            .map {
                rowToUser(it)
            }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }
    //================ NoteRepo ==================//


    suspend fun addNote(note: Note, email: String) {
        dbQuery {
            NoteTable.insert { nt ->
                nt[NoteTable.id] = note.id
                nt[NoteTable.userEmail] = email
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    suspend fun gateAllNote(email: String): List<Note> =
        dbQuery {
            NoteTable.select {
                NoteTable.userEmail.eq(email)
            }.mapNotNull {
                rowToNote(it)
            }
        }

    suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) { nt ->
                nt[NoteTable.noteTitle] = note.noteTitle
                nt[NoteTable.description] = note.description
                nt[NoteTable.date] = note.date
            }
        }
    }

    suspend fun deleteNote(id: String) {
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.id.eq(id)
            }
        }
    }


    private fun rowToNote(row: ResultRow?): Note? {
        if (row == null) {
            return null
        }
        return Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }


}