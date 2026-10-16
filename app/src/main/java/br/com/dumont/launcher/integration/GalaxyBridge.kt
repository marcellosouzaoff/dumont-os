package br.com.dumont.launcher.integration

import android.content.Intent
import android.provider.Settings
import br.com.dumont.launcher.model.AppEntry

/**
 * Public Android contracts and launchable Samsung applications only.
 * DUMONT never calls hidden Samsung services, privileged Knox APIs, or root.
 */
data class GalaxyAction(
    val id: String,
    val title: String,
    val description: String,
    val section: String
)

object GalaxyBridge {
    val systemActions = listOf(
        GalaxyAction("internet", "Internet", "Wi-Fi, dados e redes disponíveis", "Conexões"),
        GalaxyAction("wifi", "Wi-Fi", "Configurações oficiais da rede sem fio", "Conexões"),
        GalaxyAction("bluetooth", "Bluetooth", "Dispositivos e acessórios", "Conexões"),
        GalaxyAction("vpn", "VPN", "Conexões privadas", "Conexões"),
        GalaxyAction("display", "Tela", "Brilho, resolução e aparência", "Dispositivo"),
        GalaxyAction("sound", "Som", "Volume, saída de áudio e toques", "Dispositivo"),
        GalaxyAction("battery", "Bateria", "Economia de energia e consumo", "Dispositivo"),
        GalaxyAction("storage", "Armazenamento", "Espaço e gestão de arquivos", "Dispositivo"),
        GalaxyAction("accessibility", "Acessibilidade", "Leitura, interação e ajustes", "Dispositivo"),
        GalaxyAction("privacy", "Privacidade", "Permissões e indicadores de privacidade", "Segurança"),
        GalaxyAction("security", "Segurança", "Proteção de tela e credenciais", "Segurança"),
        GalaxyAction("apps", "Aplicativos", "Apps, padrões e permissões no Android", "Sistema"),
        GalaxyAction("notifications", "Notificações", "Preferências de notificações Android", "Sistema"),
        GalaxyAction("wallpaper", "Papel de parede Samsung", "Personalização do Android e One UI", "Sistema")
    )
    val publicSettingsIds: Set<String> = systemActions.map { it.id }.toSet()

    val samsungPackages = listOf(
        GalaxyApp("com.sec.android.app.myfiles", "Meus Arquivos"),
        GalaxyApp("com.sec.android.gallery3d", "Galeria"),
        GalaxyApp("com.sec.android.app.camera", "Câmera"),
        GalaxyApp("com.samsung.android.app.contacts", "Contatos"),
        GalaxyApp("com.samsung.android.app.notes", "Samsung Notes"),
        GalaxyApp("com.samsung.android.app.routines", "Modos e Rotinas"),
        GalaxyApp("com.samsung.android.oneconnect", "SmartThings"),
        GalaxyApp("com.samsung.android.lool", "Assistência do aparelho"),
        GalaxyApp("com.sec.android.app.samsungapps", "Galaxy Store"),
        GalaxyApp("com.sec.android.app.voicenote", "Gravador de Voz"),
        GalaxyApp("com.samsung.android.dialer", "Telefone")
    )

    /**
     * Only shows package activities that a regular Android launcher can actually start.
     * No promise of hidden system components or apps on other profiles.
     */
    fun availableSamsungApps(apps: List<AppEntry>): List<AppEntry> =
        samsungPackages.mapNotNull { known ->
            apps.firstOrNull { it.packageName == known.packageName }
        }.distinctBy { it.packageName }

    fun intentFor(id: String): Intent? {
        val action = when (id) {
            "internet" -> Settings.Panel.ACTION_INTERNET_CONNECTIVITY
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "vpn" -> Settings.ACTION_VPN_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "battery" -> Settings.ACTION_BATTERY_SAVER_SETTINGS
            "storage" -> Settings.ACTION_INTERNAL_STORAGE_SETTINGS
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            "privacy" -> Settings.ACTION_PRIVACY_SETTINGS
            "security" -> Settings.ACTION_SECURITY_SETTINGS
            "apps" -> Settings.ACTION_APPLICATION_SETTINGS
            "notifications" -> Settings.ACTION_NOTIFICATION_SETTINGS
            "wallpaper" -> Settings.ACTION_WALLPAPER_SETTINGS
            else -> return null
        }
        return Intent(action)
    }
}

data class GalaxyApp(val packageName: String, val label: String)
