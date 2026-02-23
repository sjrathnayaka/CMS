// JSEncrypt is loaded globally via CDN in index.html (jsencrypt.min.js)
// eslint-disable-next-line no-undef
const JSEncrypt = window.JSEncrypt
import api from '../api/api'

let _cachedPublicKey = null

/**
 * Fetches the RSA public key from the backend once and caches it.
 * @returns {Promise<string>} PEM-formatted public key
 */
export async function fetchPublicKey() {
    if (_cachedPublicKey) return _cachedPublicKey

    const response = await api.get('/encryption/public-key')
    _cachedPublicKey = response.data.publicKey
    return _cachedPublicKey
}

/**
 * Encrypts a single string value using the RSA public key.
 * @param {string} publicKey  PEM public key
 * @param {string} value      Plain-text value to encrypt
 * @returns {string}          Base64-encoded RSA ciphertext
 */
export function encryptField(publicKey, value) {
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(publicKey)
    const encrypted = encrypt.encrypt(String(value))
    if (!encrypted) {
        throw new Error('RSA encryption failed — check the public key.')
    }
    return encrypted
}
