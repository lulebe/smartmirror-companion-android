package de.lulebe.smartmirror.data


class GoogleSpeechKey(
        val type: String,
        val project_id: String,
        val private_key_id: String,
        val private_key: String,
        val client_email: String,
        val client_id: String,
        val auth_uri: String,
        val token_uri: String,
        val auth_provider_x509_cert_url: String,
        val client_x509_cert_url: String
)