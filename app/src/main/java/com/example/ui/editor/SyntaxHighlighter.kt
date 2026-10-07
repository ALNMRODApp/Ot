package com.example.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxFunction
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxPunctuation
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxType
import com.example.ui.theme.SyntaxVariable
import com.example.ui.theme.VsCodeTextPrimary
import java.util.regex.Pattern

object SyntaxHighlighter {

    private val KEYWORDS = setOf(
        // Python
        "def", "class", "import", "from", "as", "return", "if", "elif", "else", "for", "while",
        "try", "except", "finally", "with", "lambda", "yield", "raise", "pass", "break",
        "continue", "global", "nonlocal", "assert", "del", "async", "await", "True", "False", "None",
        "in", "is", "not", "and", "or",
        // JavaScript / TypeScript
        "function", "const", "let", "var", "export", "default", "new", "typeof", "instanceof",
        "this", "super", "extends", "implements", "interface", "type", "enum", "null", "undefined",
        "switch", "case", "default", "throw", "catch", "void",
        // Kotlin
        "val", "fun", "package", "data", "sealed", "object", "companion", "override", "open",
        "private", "protected", "public", "internal", "lateinit", "suspend", "by", "it"
    )

    private val TYPES = setOf(
        "int", "str", "float", "bool", "list", "dict", "set", "tuple",
        "String", "Int", "Double", "Float", "Boolean", "Long", "Any", "Unit", "List", "Map", "Set",
        "number", "string", "boolean", "any", "void", "Array", "Promise", "Record"
    )

    fun highlight(code: String, language: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            if (code.isEmpty()) return@buildAnnotatedString

            // Base styling
            addStyle(SpanStyle(color = VsCodeTextPrimary), 0, code.length)

            // 1. Strings: "..." and '...'
            val stringMatcher = Pattern.compile("(\"[^\"]*\"|'[^']*')").matcher(code)
            while (stringMatcher.find()) {
                addStyle(
                    SpanStyle(color = SyntaxString),
                    stringMatcher.start(),
                    stringMatcher.end()
                )
            }

            // 2. Comments: #... or //...
            val commentMatcher = Pattern.compile("(#[^\\n]*|//[^\\n]*)").matcher(code)
            while (commentMatcher.find()) {
                addStyle(
                    SpanStyle(color = SyntaxComment, fontStyle = FontStyle.Italic),
                    commentMatcher.start(),
                    commentMatcher.end()
                )
            }

            // 3. Numbers: integers and floats
            val numberMatcher = Pattern.compile("\\b(\\d+(\\.\\d+)?)\\b").matcher(code)
            while (numberMatcher.find()) {
                addStyle(
                    SpanStyle(color = SyntaxNumber),
                    numberMatcher.start(),
                    numberMatcher.end()
                )
            }

            // 4. Function definitions & invocations: word followed by (
            val functionMatcher = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*(?=\\()").matcher(code)
            while (functionMatcher.find()) {
                val word = functionMatcher.group(1)
                if (word !in KEYWORDS) {
                    addStyle(
                        SpanStyle(color = SyntaxFunction, fontWeight = FontWeight.Normal),
                        functionMatcher.start(1),
                        functionMatcher.end(1)
                    )
                }
            }

            // 5. Keywords and Types
            val wordMatcher = Pattern.compile("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\b").matcher(code)
            while (wordMatcher.find()) {
                val word = wordMatcher.group(1)
                if (word in KEYWORDS) {
                    addStyle(
                        SpanStyle(color = SyntaxKeyword, fontWeight = FontWeight.SemiBold),
                        wordMatcher.start(),
                        wordMatcher.end()
                    )
                } else if (word in TYPES) {
                    addStyle(
                        SpanStyle(color = SyntaxType, fontWeight = FontWeight.Normal),
                        wordMatcher.start(),
                        wordMatcher.end()
                    )
                }
            }
        }
    }
}
