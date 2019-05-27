package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.view.Screens
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

//use when app launched by uri
fun Router.createStartChain(vararg args: Screen) {
    newRootChain(Screens.MainPassenger(), *args)
}


//clear navigation stack and got back to root (by default to Main)
fun Router.finishChainAndBackTo(screen: Screen = Screens.MainPassenger()) {
    finishChain()
    backTo(screen)
}

//use if need delete current chain, go back to main and create new chain
fun Router.newChainFromMain(vararg args: Screen) {
    finishChainAndBackTo(Screens.MainPassenger())
    newRootChain(*args)
}