package com.example.localbrowser

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status

class LocalWebServer(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession?): Response {
        return try {
            when (session?.uri) {
                "/" -> serveHtml(getHomePage())
                "/style.css" -> serveCss(getStyles())
                "/script.js" -> serveJs(getJavaScript())
                else -> newFixedLengthResponse(Status.NOT_FOUND, "text/html", "<h1>404 Not Found</h1>")
            }
        } catch (e: Exception) {
            newFixedLengthResponse(Status.INTERNAL_ERROR, "text/html", "<h1>500 Server Error</h1>")
        }
    }

    private fun serveHtml(content: String): Response {
        return newFixedLengthResponse(Status.OK, "text/html; charset=utf-8", content)
    }

    private fun serveCss(content: String): Response {
        return newFixedLengthResponse(Status.OK, "text/css", content)
    }

    private fun serveJs(content: String): Response {
        return newFixedLengthResponse(Status.APPLICATION_JSON, "application/javascript", content)
    }

    private fun getHomePage(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Local Browser</title>
                <link rel="stylesheet" href="/style.css">
            </head>
            <body>
                <div class="container">
                    <h1>Local Web Browser</h1>
                    <div class="input-group">
                        <input type="text" id="urlInput" placeholder="Enter URL (http://example.com)" value="http://example.com">
                        <button onclick="navigateTo()">Go</button>
                    </div>
                    <div class="info">
                        <p>This is a simple local web server running on your Android device.</p>
                        <p>You can browse web content from this device or other devices on your network.</p>
                    </div>
                    <div class="content">
                        <h2>Welcome</h2>
                        <p>Server is running successfully!</p>
                    </div>
                </div>
                <script src="/script.js"></script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun getStyles(): String {
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 20px;
            }

            .container {
                background: white;
                border-radius: 10px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                padding: 40px;
                max-width: 600px;
                width: 100%;
            }

            h1 {
                color: #333;
                margin-bottom: 30px;
                text-align: center;
            }

            .input-group {
                display: flex;
                gap: 10px;
                margin-bottom: 30px;
            }

            input[type="text"] {
                flex: 1;
                padding: 12px;
                border: 2px solid #ddd;
                border-radius: 5px;
                font-size: 16px;
                transition: border-color 0.3s;
            }

            input[type="text"]:focus {
                outline: none;
                border-color: #667eea;
            }

            button {
                padding: 12px 30px;
                background: #667eea;
                color: white;
                border: none;
                border-radius: 5px;
                font-size: 16px;
                font-weight: bold;
                cursor: pointer;
                transition: background 0.3s;
            }

            button:hover {
                background: #5568d3;
            }

            .info {
                background: #f5f5f5;
                padding: 20px;
                border-radius: 5px;
                margin-bottom: 20px;
            }

            .info p {
                color: #666;
                margin-bottom: 10px;
                line-height: 1.6;
            }

            .content {
                border-top: 2px solid #ddd;
                padding-top: 20px;
            }

            .content h2 {
                color: #333;
                margin-bottom: 10px;
            }

            .content p {
                color: #666;
                line-height: 1.6;
            }
        """.trimIndent()
    }

    private fun getJavaScript(): String {
        return """
            function navigateTo() {
                const urlInput = document.getElementById('urlInput');
                const url = urlInput.value;
                
                if (url.trim()) {
                    console.log('Would navigate to: ' + url);
                }
            }

            document.getElementById('urlInput').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    navigateTo();
                }
            });
        """.trimIndent()
    }
}
