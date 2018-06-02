/* vim: set tabstop=4 shiftwidth=4 expandtab: */
package org.cyanogenmod.platform.internal;

import com.android.server.SystemService;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import cyanogenmod.app.CMContextConstants;
import cyanogenmod.app.ContainerManager;
import cyanogenmod.app.IContainerManager;

import com.spotify.docker.client.*;
import com.spotify.docker.client.exceptions.*;

/**
 * Internal service which manages interactions with the phone and data connection
 *
 * @hide
 */
public class ContainerManagerService extends CMSystemService {
    private static final String TAG = "ContainerManagerSrv";
    private static boolean localLOGD = Log.isLoggable(TAG, Log.DEBUG);
    private DockerClient docker;

    private Context mContext;
    private final IBinder mService = new IContainerManager.Stub() {

        @Override
        public void sayHelloTo(String msg) {
            enforceContainerSayHelloPermission();
            ContainerManagerService.this.sayHelloTo(msg);
        }
    };

    public ContainerManagerService(Context context) {
        super(context);
        mContext = context;
        try {
            docker = DefaultDockerClient.fromEnv().build();
            Log.d(TAG, "Initialized Docker client: " + docker);
        } catch (DockerCertificateException e) {
            Log.e(TAG, "Could not initialize Docker client");
        }
    }

    @Override
    public String getFeatureDeclaration() {
        return CMContextConstants.Features.CONTAINERS;
    }

    @Override
    public void onStart() {
        if (localLOGD) {
            Log.d(TAG, "Container Manager Service start: " + this);
        }
        publishBinderService(CMContextConstants.CM_CONTAINER_MANAGER_SERVICE, mService);
    }

    private void sayHelloTo(String msg) {
        Log.e(TAG, "ContainerManagerService: Say hello to " + msg + "!");
    }

    private void enforceContainerSayHelloPermission() {
        mContext.enforceCallingOrSelfPermission(
                cyanogenmod.platform.Manifest.permission.CONTAINER_SAY_HELLO,
                "ContainerManagerService");
    }
}
