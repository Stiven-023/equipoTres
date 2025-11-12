/*package com.univalle.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.univalle.equipotres.view.MainActivity
import com.univalle.equipotres.R

class MyAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Se actualizan todos los widgets activos
        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Crea un intent para abrir la app cuando se toque el widget
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Crea la vista del widget usando el layout XML
            val views = RemoteViews(context.packageName, R.layout.app_widget)

            // Configura el texto o imagen del widget
            views.setTextViewText(R.id.widget_text, "¡Hola desde Equipotres!")

            // Configura la acción al tocar el widget
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Actualiza el widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}*/

package com.univalle.equipotres.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.univalle.equipotres.R
import com.univalle.equipotres.database.AppDatabase
import com.univalle.equipotres.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Actualiza todos los widgets activos
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.app_widget)

            // Acción para abrir la app al tocar el widget
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Obtener el total del inventario desde la BD local (Room)
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val totalBalance = db.productDao().getTotalInventoryValue() ?: 0.0

                // Actualiza el texto del widget
                views.setTextViewText(
                    R.id.tvSaldoTotal,
                    "💰 Saldo total: $%.2f".format(totalBalance)
                )

                // Aplica la actualización
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}

