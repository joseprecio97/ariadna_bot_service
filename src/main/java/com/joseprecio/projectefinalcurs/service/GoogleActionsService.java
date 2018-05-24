package com.joseprecio.projectefinalcurs.service;

import java.io.IOException;

import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSDKResponse;
import com.joseprecio.projectefinalcurs.model.googleactionssdk.GoogleActionsSdkInformation;

public interface GoogleActionsService {

	public GoogleActionsSdkInformation getInfo() throws IOException;

	public void setInfo(GoogleActionsSdkInformation info) throws IOException;

	GoogleActionsSDKResponse updateGoogleActions(String token) throws Exception;

}
