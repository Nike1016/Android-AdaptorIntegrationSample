#Android Adaptor Integration Usage Sample

Before you begin implementing your adapter, please ensure that you have the following  in place:

1. Ensure that you include the link to the Burstly SDK (enclosed) in your classpath.
2. Review the Java Docs (enclosed) for the following interfaces
     - com.burstly.lib.component.IBurstlyAdaptor
     - com.burstly.lib.feature.networks.IAdaptorFactory
3. Review the enclosed samples:
	- Meganet is a hypothetical network that implements the above interfaces. (enclosed: IntegrationSample)
    - A simple Android application that invokes Meganet ads via the Burstly SDK. (enclosed: IntegrationUsageSample)
4. Login to the Burstly Dashboard and get familiar about setting up an appId (unique to the application) and a zone-id (unique to the placement, banner/interstitial/etc).
5. After you are logged in, navigate to "Create an Ad" page to understand our ad set-up page. Click the button that lets you create a 3rd party ad. This page contains fields that publishers will use to pass in "network specific" data that you would use in your SDK. This field is a JSON encoded string that contains multiple key-value pairs. Note that on successfully integrating your adapters, our UI will be modified to reflect the fields required
6. The JSON string is passed in to your adapter via the Burstly SDK and the key-value pairs are transferred to a map. [Visit the JavaDoc for IBurstlyAdapter for more details]
7. Note that you are provided with two samples (one that shows a sample implementation of an adapter and another app that requests ads via the aforementioned adapter)
8. If you wish to receive ad-network specific data from your publisher, have them implement the following method:
BursltyView.setClientTargetParams(Map). This map will be accessible in IBurstlyAdaptor.startTransaction(Map) under the TargetingParameter.KEY key.

###Integration:

1. Implement the following interfaces
	- com.burstly.lib.component.IBurstlyAdaptor
	- com.burstly.lib.feature.networks.IAdaptorFactory
2. Compile the implementation in to a simple jar file.(eg: meganet.jar)
3. Provide us with a sample JSON object of the request params required by your adapter. Contact accounts@burstly.com with this information.
		
		Example:
		meganet?{"factoryClass":"com.meganet.integration.MeganetAdaptorFactory", "imageUrl":"http://img263.imageshack.us/img263/6085/samplen.png"}		
Note: "factoryClass" is a mandatory JSON parameter. The value of this parameter has to be the FULL class name of the class that implements IAdaptorFactory, in case of our example the name is com.meganet.integration.MeganetAdaptorFactory.
4. You will be provided with a sample application that lets you test your placements via your adapter.
5. Run the app and check if your banner/interstitial units serve as expected.