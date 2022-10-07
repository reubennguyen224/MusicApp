package com.rikkei.training.musicapp.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadUtil(activity: Activity) {
    var activity = activity
    var serverURL = "https://hoang2204.000webhostapp.com/server/uploadImage.php"
    var serverUploadPath = "https://hoang2204.000webhostapp.com/img/userAvatar/"
    val client = OkHttpClient()

    fun uploadFile(sourceFilePath: String, uploadedFileName: String? = null) {
        uploadFile(File(sourceFilePath), uploadedFileName)
    }

    fun uploadFile(sourceFileUri: Uri, uploadedFileName: String? = null) {
        val pathFromUri = URIPathHelper().getPath(activity, sourceFileUri)
        uploadFile(File(pathFromUri), uploadedFileName)
    }

    fun uploadFile(sourceFile: File, uploadedFileName: String? = null) {
        Thread {
            val mimeType = getMIMEType(sourceFile)
            if (mimeType == null) {
                Log.e("ERROR_FILE", "Can't get mime type of file")
                return@Thread
            }
            val fileName: String = uploadedFileName ?: sourceFile.name

            try {
                val requestBody: RequestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "uploaded_file",
                        fileName,
                        sourceFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    )
                    .build()
                val request: Request = Request.Builder().url(serverURL).post(requestBody).build()

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("File Upload", "Upload File success")
                } else {
                    Log.e("File Upload", "Upload File Failed")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }.start()
    }

    private fun getMIMEType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}

class URIPathHelper {
    fun getPath(context: Context, uri: Uri): String? {
        val isKit = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // >= adnroid 4

        if (isKit && DocumentsContract.isDocumentUri(context, uri)) {
            if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null

                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}