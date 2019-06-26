package com.kg.gettransfer.remote

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http.HttpHeaders.contentLength
import okhttp3.internal.http.StatusLine.HTTP_CONTINUE
import okio.Buffer
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.slf4j.Logger
import java.io.EOFException
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * same as HttpLoggingInterceptor except logging header with password, line 52
 * TODO: remove this 💩
 */
class PrivateHttpLoggingInterceptor : Interceptor, KoinComponent {

    private val utf8 = Charset.forName("UTF-8")
    private val peekBodySize: Long = java.lang.Long.MAX_VALUE

    private val logger: Logger by inject { parametersOf("GTR-remote") }

    @Suppress("MagicNumber")
    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size() < 64) buffer.size() else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    @Suppress("LongMethod", "ComplexMethod", "NestedBlockDepth")
    override fun intercept(chain: Interceptor.Chain): Response {
        val logBody = logger.isDebugEnabled
        val path = chain.request().url().url().path
        val logHeaders = (logBody || logger.isInfoEnabled) &&
            !(path?.contains(Api.API_LOGIN) ?: false) &&
            !(path?.contains(ApiCore.PARAM_API_KEY) ?: false)

        val request = chain.request()

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        val protocol = connection?.protocol() ?: Protocol.HTTP_1_1

        logger.info("--> {} {} {}", request.method(), request.url(), protocol)

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor.
                // Force them to be included (when available) so there values are known.
                requestBody?.contentType()?.let { logger.info("Content-Type: {}", it) }

                requestBody?.let { rb ->
                    if (rb.contentLength() != -1L) {
                        logger.info("Content-Length: {}", rb.contentLength())
                    }
                }
            }

            val headers = request.headers()
            for (i in 0 until headers.size()) {
                val name = headers.name(i)

                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(name, ignoreCase = true) &&
                    !"Content-Length".equals(name, ignoreCase = true)) {

                    logger.info("{}: {}", name, headers.value(i))
                }
            }

            if (!logBody || !hasRequestBody) {
                logger.info("--> END {}", request.method())
            } else if (bodyEncoded(request.headers())) {
                logger.info("--> END {} (encoded body omitted)", request.method())
            } else {
                val buffer = Buffer()
                requestBody?.writeTo(buffer)

                val charset = requestBody?.contentType()?.charset(utf8) ?: utf8
                logger.debug("") // new line
                if (isPlaintext(buffer)) {
                    logger.debug(buffer.readString(charset))
                    logger.debug("--> END {} ({}-byte body)", request.method(), requestBody?.contentLength())
                } else {
                    logger.debug(
                        "--> END {} (binary {}-byte body omitted",
                        request.method(),
                        requestBody?.contentLength()
                    )
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            logger.info("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody?.contentLength() ?: -1L
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"

        logger.info(
            "<-- {} {} {} (took {} ms {})",
            response.code(),
            response.message(),
            response.request().url(),
            tookMs,
            if (!logHeaders) ", $bodySize body" else ""
        )

        if (logHeaders) {
            val headers = response.headers()
            for (i in 0 until headers.size()) logger.info("{}: {}", headers.name(i), headers.value(i))

            if (!logBody || !hasBody(response)) {
                logger.info("<-- END HTTP")
            } else if (bodyEncoded(headers)) {
                logger.info("<-- END HTTP (encoded body omitted)")
            } else {
                responseBody?.source()?.let { src ->
                    src.request(peekBodySize)
                    val buffer = src.buffer()

                    if (!isPlaintext(buffer)) {
                        logger.debug("")
                        logger.debug("<-- END HTTP (binary {}-byte body omitted)", contentLength)
                        return response
                    }

                    if (contentLength > 0L) {
                        logger.debug("")
                        val charset = responseBody.contentType()?.charset(utf8) ?: utf8
                        logger.debug(buffer.clone().readString(charset))
                    }
                }

                logger.debug("<-- END HTTP ({}-byte body)", contentLength)
            }
        }
        return response
    }

    private fun hasBody(response: Response): Boolean =
        // HEAD requests never yield a body regardless of the response headers.
        if (response.request().method() == "HEAD") {
            false
        } else {
            @Suppress("ComplexCondition", "MagicNumber")
            if ((response.code() < HTTP_CONTINUE || response.code() >= 200) &&
                response.code() != HTTP_NO_CONTENT &&
                response.code() != HTTP_NOT_MODIFIED) {
                true
            } else {
                // If the Content-Length or Transfer-Encoding headers disagree with the response code, the
                // response is malformed. For best compatibility, we honor the headers.
                contentLength(response) != -1L ||
                    "chunked".equals(response.header("Transfer-Encoding"), ignoreCase = true)
            }
        }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    enum class PrivateData(val query: String) {
        PASSWORD("password")
    }
}
