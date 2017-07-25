package com.github.wakingrufus.mastodon.ui

import com.sys1yagi.mastodon4j.api.entity.Status
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import mu.KLogging


class FeedsController(private val feedStates: ObservableList<ObservableList<Status>>) {
    companion object : KLogging()

    @FXML
    internal var feedsWrapper: HBox? = null

    fun initialize() {
        feedStates.forEach {
            feedsWrapper?.children?.add(buildTootFeedPanel(it))

        }

        feedStates.addListener { change: ListChangeListener.Change<out ObservableList<Status>>? ->
            while (change?.next()!!) {
                change.addedSubList?.forEach { feedsWrapper?.children?.add(element = buildTootFeedPanel(it), index = 0) }
            }
        }
    }

    fun buildTootFeedPanel(feedState: ObservableList<Status>): ScrollPane {
        val tootController = TootFeedController(feedState)
        val fxmlLoader = FXMLLoader(javaClass.getResource("/toot-feed.fxml"))
        fxmlLoader.setController(tootController)
        return fxmlLoader.load()
    }
}