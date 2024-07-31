package com.example.whatsappclone.models

sealed class UpdateStatus {
    object processing : UpdateStatus()
            object processed:UpdateStatus()
}