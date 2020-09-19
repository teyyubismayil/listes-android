package com.teyyub.listes

import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    //Here we will save selected navigation item id
    //to restore after configuration change
    var selectedNavigationItem: Int? = null
}