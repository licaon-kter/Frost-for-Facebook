package com.pitchedapps.frost.iitems

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ca.allanwang.kau.iitems.KauIItem
import ca.allanwang.kau.ui.createSimpleRippleDrawable
import ca.allanwang.kau.utils.bindView
import ca.allanwang.kau.utils.gone
import ca.allanwang.kau.utils.visible
import ca.allanwang.kau.utils.withAlpha
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.pitchedapps.frost.R
import com.pitchedapps.frost.glide.FrostGlide
import com.pitchedapps.frost.glide.GlideApp
import com.pitchedapps.frost.parsers.FrostNotif
import com.pitchedapps.frost.services.FrostRunnable
import com.pitchedapps.frost.utils.Prefs
import com.pitchedapps.frost.utils.launchWebOverlay

/**
 * Created by Allan Wang on 27/12/17.
 */
class NotificationIItem(val notification: FrostNotif, val cookie: String) : KauIItem<NotificationIItem, NotificationIItem.ViewHolder>(
        R.layout.iitem_notification, ::ViewHolder
) {

    companion object {
        fun bindEvents(adapter: ItemAdapter<NotificationIItem>) {
            adapter.fastAdapter.withSelectable(false)
                    .withOnClickListener { v, _, item, position ->
                        val notif = item.notification
                        if (notif.unread) {
                            FrostRunnable.markNotificationRead(v.context, notif.id, item.cookie)
                            adapter.set(position, NotificationIItem(notif.copy(unread = false), item.cookie))
                        }
                        v.context.launchWebOverlay(notif.url)
                        true
                    }
        }
    }

    class ViewHolder(itemView: View) : FastAdapter.ViewHolder<NotificationIItem>(itemView) {

        val frame: ViewGroup by bindView(R.id.item_frame)
        val avatar: ImageView by bindView(R.id.item_avatar)
        val content: TextView by bindView(R.id.item_content)
        val date: TextView by bindView(R.id.item_date)
        val thumbnail: ImageView by bindView(R.id.item_thumbnail)

        private val glide
            get() = GlideApp.with(itemView)

        override fun bindView(item: NotificationIItem, payloads: MutableList<Any>) {
            val notif = item.notification
            frame.background = createSimpleRippleDrawable(Prefs.textColor,
                    Prefs.nativeBgColor(notif.unread))
            content.setTextColor(Prefs.textColor)
            date.setTextColor(Prefs.textColor.withAlpha(150))

            val glide = glide
            glide.load(notif.img)
                    .transform(FrostGlide.roundCorner)
                    .into(avatar)
            if (notif.thumbnailUrl != null)
                glide.load(notif.thumbnailUrl).into(thumbnail.visible())

            content.text = notif.content
            date.text = notif.timeString
        }

        override fun unbindView(item: NotificationIItem) {
            frame.background = null
            val glide = glide
            glide.clear(avatar)
            glide.clear(thumbnail)
            thumbnail.gone()
            content.text = null
            date.text = null
        }
    }
}