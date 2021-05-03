# ryd pay Web SDK integration sample
This sample illustrates how to integrate the ryd pay Web SDK into a native Android 
application by using Chrome Custom tabs. There are several additional ways of handling the same
functionality such as implementing a dedicated WebView or even launching an external browser. In all
case the results of the ryd pay web sdk will be delivered back to the app through a custom url schema
described in the dedicated section below.

### General overview of this sample

![](docs/images/sample_diagram.png)

This sample application has a dedicated Activity (`CCTHandlerActivity`) that initializes and starts a
Chrome Custom Tab session with an input url of the ryd pay web sdk. In addition, it has registered 
in the Android Manifest a custom url schema so that it will be reopened once the top up flow in the
web is complete. Whenever this happens it tries to parse the data from the callback url and deliver it
through the standard Android ActivityResult API to the caller Activity that started it.

### Implementation details

##### Launching the ryd pay web
The base url of the testing environment is: `https://ryd-demo.web.app`. It expects the following url
query parameters:
- `callback=true` - **Required**. In order for the web sdk to trigger the custom callback url schema,
you have to send this paramenter.
- `pid={POI id}` - **Optional**. The POI id of a particular station. In case it is not provided the 
web SDK will try to find a close enough ryd pay station around the user location.


##### Consuming results of the ryd pay web
The ryd pay web sdk delivers back results by calling the custom url schema with aditional host 
and url query parameters. Successful top ups result in calling the `finish` host with additional
url query parameter `paymentdata` url encoded json payload. The JSON contains payment details data, such
as amount of fuel topped up, total price and price per liter. Here is an example callback url

```rydpaywebsdk://finish?paymentdata=%7B%22amount%22%3A10.43%2C%22price%22%3A1.879%2C%22total%22%3A19.6%2C%22stationId%22%3A%225f746bf05bce72222d327778%22%7D```

The url decoded version of the payment data looks like this:

```{"amount":10.43,"price":1.879,"total":19.6,"stationId":"5f746bf05bce72222d327778"}```