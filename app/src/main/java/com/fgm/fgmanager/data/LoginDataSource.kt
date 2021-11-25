package com.fgm.fgmanager.data

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation
import com.fgm.fgmanager.R
import com.fgm.fgmanager.data.model.LoggedInUser
import com.fgm.fgmanager.ui.login.LoginFragment
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        if(username == "1" && password == "12345q"){
            //Log.i("222", "WORK")
            val User = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            return Result.Success(User)
        }else
            return Result.NotSuccess("Ты кто такой!?") // И здесь надпись не нужна
        /*try {
            if(username == "1" && password == "12345q"){
                //Log.i("222", "WORK")
                val User = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                return Result.Success(User)
            }else
                return Result.NotSuccess("")
            //val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
            //return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }*/
    }

    fun logout() {
        // TODO: revoke authentication
    }
}