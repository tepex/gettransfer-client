package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.view.Screens
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

//use when app launched by uri
fun Router.createStartChain(vararg args: Screen) {
    newRootChain(Screens.MainPassenger(), *args)
}

//clear navigation stack and go back to root
//backTo() without finishChain doesn't work
fun Router.finishChainAndBackTo(screen: Screen) {
    finishChain()
    newRootScreen(screen)
}

//use if need to delete current chain, go back to main and create new chain
fun Router.newChainFromMain(vararg args: Screen) {
    finishChain()
    newRootChain(Screens.MainPassenger(), *args)
}