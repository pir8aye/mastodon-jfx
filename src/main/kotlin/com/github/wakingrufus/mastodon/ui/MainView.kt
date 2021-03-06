package com.github.wakingrufus.mastodon.ui

import com.github.wakingrufus.mastodon.account.createAccountState
import com.github.wakingrufus.mastodon.client.createAccountClient
import com.github.wakingrufus.mastodon.config.ConfigurationHandler
import com.github.wakingrufus.mastodon.config.FileConfigurationHandler
import com.github.wakingrufus.mastodon.data.AccountState
import com.github.wakingrufus.mastodon.data.NotificationFeed
import com.github.wakingrufus.mastodon.data.StatusFeed
import javafx.collections.FXCollections
import mu.KLogging
import org.controlsfx.control.Notifications
import tornadofx.*
import java.io.File

class MainView : View() {
    companion object : KLogging()

    val accountStates = FXCollections.observableArrayList<AccountState>()
    val statusFeeds = FXCollections.observableArrayList<StatusFeed>()
    val settingsView = find<SettingsView>(mapOf(
            "accountStates" to accountStates,
            "createAccount" to this::newAccount,
            "viewNotifications" to this::viewNotifications,
            "viewFeed" to this::viewFeed))
    val statusFeedsView = find<StatusFeedsView>(mapOf(
            "statusFeeds" to statusFeeds,
            "accounts" to accountStates))

    val configHandler: ConfigurationHandler =
            FileConfigurationHandler(File(File(System.getProperty("user.home")), ".mastodon.txt"))

    override val root = borderpane {
        minHeight = 100.percent.value
        //   center(FeedsController::class)
        left = settingsView.root

    }

    fun viewFeed(statusFeed: StatusFeed) {
        statusFeeds.add(statusFeed)
        viewFeeds()
    }

    fun viewNotifications(notificationFeed: NotificationFeed) {
        root.right = find<NotificationFeedView>(mapOf("notificationFeed" to notificationFeed)).root
    }

    fun viewFeeds() {
        root.center = statusFeedsView.root
    }

    fun newAccount() {
        root.center = find<OAuthView>(mapOf("onComplete" to { a: AccountState -> accountStates.add(a) })).root
    }

    init {
        //   controller.readConfig()
        configHandler.readFileConfig().identities.forEach { (accessToken, _, _, _, server) ->
            val client = createAccountClient(server, accessToken)
            accountStates.add(createAccountState(client = client, onNotification = {
                Notifications.create()
                    //    .text(it.type)
                        .graphic(find<NotificationFragment>(params = mapOf("notification" to it, "server" to server)).root)
                        .darkStyle()
                        .show()
            }))
        }
    }


}