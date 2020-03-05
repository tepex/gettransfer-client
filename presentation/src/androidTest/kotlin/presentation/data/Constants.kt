package com.kg.gettransfer.presentation.data

object Constants {
    // For UI-tests
    // Strings value for language
    const val TEXT_RUSSIANLANGUAGE = "Русский"
    // Strings time unix
    const val dayInUnix = 86400
    const val four = 4
    const val thirtyOne = 31
    const val oneThousand = 1000
    // Strings time sleep state
    const val small = 1_000L
    const val medium = 3_500L
    const val big = 10_000L
    // Strings time sleep state end
    const val TEXT_OK = "OK"
    const val TEXT_CANCEL = "CANCEL"
    const val TEXT_REASON = "The changed requirements"
    // Partner account
    const val TEXT_EMAIL_BALANCE = "r.abdullina@gettransfer.com"
    const val TEXT_PWD_BALANCE = "123456"
    // Passenger account
    const val TEXT_EMAIL_PASSENGER = "mygtracc1@gmail.com"
    const val TEXT_PHONE_PASSENGER = "+79992223838"
    const val TEXT_PWD_PASSENGER = "PassRR11"
    const val TEXT_EMAIL_IVAN = "r.abdullina+56@gettransfer.com"
    const val TEXT_EMAIL_SIGN_UP = "r.abdullina+536@gettransfer.com"
    const val TEXT_RECARDO = "Ricardo"
    const val TEXT_NUMBER_IVAN = "79116789567"
    const val TEXT_NUMBER_SIGN_UP = "79263289567"
    const val TEXT_RANDOM_NUMBER = "123456"
    const val TEXT_PHONE_IVAN = "79007777777"
    const val TEXT_PWD_IVAN = "12345"
    const val TEXT_TEST = "Test-Test"
    // Locations
    const val TEXT_PETERSBURG = "Saint-Petersburg"
    const val TEXT_PETERSBURG_SELECT = "Saint Petersburg, Россия"
    const val TEXT_MOSCOW = "Moscow"
    const val TEXT_MOSCOW_SELECT = "Moscow, Россия"
    // E-mail and api
    const val TEXT_EMAIL_MAILSLURP = "68889e96-7719-4bb8-99fc-1e96f95ce2cc@mailslurp.com"
    const val HTTP_REQUEST =
        "https://api.mailslurp.com/waitForLatestEmail?inboxId=68889e96-7719-4bb8-99fc-1e96f95ce2cc&timeout=1000"
    const val API_KEY = "ac292be85aeee46f99c9cc2be6a1bbcd84d149d601202a6e4601f8a97e348fbe"
    const val TEXT_CHANGE_EMAIL_SIM = "9ffad70e-75ab-4a22-9450-17a9c8a3a3a7@mailslurp.com"
    const val TEXT_CHANGE_PASSWORD_SIM = "Vcenicegod1"
    const val TEXT_NEW_NAME_SIM = "SimSimSimSimSimSim"
    const val TEXT_NEW_EMAIL_SIM = "f1005acf-74f8-47c0-adcf-88d935bea8d6@mailslurp.com"
    const val TEXT_URL_CHANGE_EMAIL =
        "https://api.mailslurp.com/waitForLatestEmail?inboxId=f1005acf-74f8-47c0-adcf-88d935bea8d6&timeout=1000"
    const val TEXT_API_CHANGE_EMAIL = "0de1d2e9ea9e0b4af88bbafb2ac8047e655005a4be718cb816d805653d78e1e5"
    const val TEXT_URL_EMAIL_SIM =
        "https://api.mailslurp.com/waitForLatestEmail?inboxId=9ffad70e-75ab-4a22-9450-17a9c8a3a3a7&timeout=1000"
    const val TEXT_API_EMAIL_SIM = "a5f354b0b0d4822ecb13f32be94784916c913e0e94633e16d881634930327aae"
    const val TEXT_URL_EMPTY_INBOX = "https://api.mailslurp.com/emptyInbox?inboxId=f1005acf-74f8-47c0-adcf-88d935bea8d6"
    const val TEXT_URL_EMPTY_INBOX_SIM =
        "https://api.mailslurp.com/emptyInbox?inboxId=9ffad70e-75ab-4a22-9450-17a9c8a3a3a7"
    const val TEXT_HEADER = "x-api-key"
    // RegEx
    const val REGEXCODE = "([\\w]){8}(?=\\\\r)"
    const val REGEXDISTANCE = "([\\d]){3}"
    const val REGEXKM = "([a-z]){2,5}"
    const val REGEX4LOGIN = """(\d){4}(?=\\r\\n\\t\</p>)"""
    // List number position items
    const val POSITION_THIRD = 3
}
