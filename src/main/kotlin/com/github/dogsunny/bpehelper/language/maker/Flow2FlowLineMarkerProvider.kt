package com.github.dogsunny.bpehelper.language.maker

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement

class Flow2FlowLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        scalaElement: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>?>
    ) {

    }

}