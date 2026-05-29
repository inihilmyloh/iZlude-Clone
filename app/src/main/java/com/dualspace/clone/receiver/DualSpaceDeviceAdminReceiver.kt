package com.dualspace.clone.receiver

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast

/**
 * Device Admin Receiver for managing the Work Profile.
 * This handles provisioning callbacks for the managed profile.
 */
class DualSpaceDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, DualSpaceDeviceAdminReceiver::class.java)
        }
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        // Profile provisioning is complete
        // Enable the profile so it's accessible
        val manager = context.getSystemService(Context.DEVICE_POLICY_SERVICE)
                as android.app.admin.DevicePolicyManager
        val componentName = getComponentName(context)

        // Set the profile name
        manager.setProfileName(componentName, "DualSpace")

        // Enable the profile
        manager.setProfileEnabled(componentName)
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "DualSpace Admin aktif", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "DualSpace Admin nonaktif", Toast.LENGTH_SHORT).show()
    }
}
