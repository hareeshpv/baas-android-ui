package com.payu.baas.core.util

import android.util.Base64
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object BaasEncryption {

    private const val characterEncoding = "UTF-8"
    private const val cipherTransformation =
        "AES/GCM/NoPadding"  // as in backend its GCM_NO_padding
    private const val aesEncryptionAlgorithm = "AES"

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(cipherText: ByteArray?, key: ByteArray?, initialVector: ByteArray?): ByteArray? {
        var cipherText = cipherText
        val cipher = Cipher.getInstance(cipherTransformation)
        val secretKeySpecy = SecretKeySpec(key, aesEncryptionAlgorithm)
        val ivParameterSpec = IvParameterSpec(initialVector)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecy, ivParameterSpec)
        cipherText = cipher.doFinal(cipherText)
        return cipherText
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(plainText: ByteArray?, key: ByteArray?, initialVector: ByteArray?): ByteArray? {
        var plainText = plainText
        val cipher = Cipher.getInstance(cipherTransformation)
        val secretKeySpec = SecretKeySpec(key, aesEncryptionAlgorithm)
        val ivParameterSpec = IvParameterSpec(initialVector)
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        plainText = cipher.doFinal(plainText)
        return plainText
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getKeyBytes(key: String): ByteArray {
        val keyBytes = ByteArray(16)
        val parameterKeyBytes = key.toByteArray(charset(characterEncoding))
        System.arraycopy(
            parameterKeyBytes, 0, keyBytes,
            0, Math.min(parameterKeyBytes.size, keyBytes.size)
        )
        return keyBytes
    }

    /// <summary>
    /// Encrypts plaintext using AES 128bit key and a Chain Block Cipher and returns a base64 encoded string
    /// </summary>
    /// <param name="plainText">Plain text to encrypt</param>
    /// <param name="key">Secret key</param>
    /// <returns>Base64 encoded string</returns>
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(plainText: String): String? {
        val plainTextbytes = plainText.toByteArray(charset(characterEncoding))
        val keyBytes = getKeyBytes("8080808080808080")

        return Base64.encodeToString(encrypt(plainTextbytes, keyBytes, keyBytes), Base64.DEFAULT)
    }

    /// <summary>
    /// Decrypts a base64 encoded string using the given key (AES 128bit key and a Chain Block Cipher)
    /// </summary>
    /// <param name="encryptedText">Base64 Encoded String</param>
    /// <param name="key">Secret Key</param>
    /// <returns>Decrypted String</returns>
    @Throws(
        KeyException::class,
        GeneralSecurityException::class,
        GeneralSecurityException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        IOException::class
    )
    fun decrypt(encryptedText: String?): String? {
        val cipheredBytes = Base64.decode(encryptedText, Base64.DEFAULT)
        val keyBytes = getKeyBytes("8080808080808080")

        return String(decrypt(cipheredBytes, keyBytes, keyBytes)!!, charset(characterEncoding))
    }

    /*
    Separate decryption method for decrypting karza key
    as encrypted in a different way at backend
     */
    @Throws(
        KeyException::class,
        GeneralSecurityException::class,
        GeneralSecurityException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class,
        IOException::class,
        IllegalStateException::class
    )
    fun decryptUrl(url: String, secret: String, deviceBindingId: String): String? {
        var decodedBase64: String? = null
        val key = deviceBindingId + secret
        var iv = ByteArray(16)
        iv = secret.substring(32, 48).toByteArray()
        val brandSecret =
            SecretKeySpec(key.substring(0, 32).toByteArray(), aesEncryptionAlgorithm)
        val cipherUrl = Cipher.getInstance(cipherTransformation)
        cipherUrl.init(Cipher.DECRYPT_MODE, brandSecret, GCMParameterSpec(128, iv))
        val encrypted: ByteArray =
            cipherUrl.doFinal(Base64.decode(url.toByteArray(), Base64.DEFAULT))
        decodedBase64 = String(Base64.decode(encrypted, Base64.DEFAULT))
        return decodedBase64
    }
}