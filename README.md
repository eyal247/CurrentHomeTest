# CurrentHomeTest
Home test for Current

This a simple native Android app with 2 activities:
1. MainActivity in which the app is requesting location permission from user, and then based on user's location it makes a Google Places API request to find all nearby Burrito joints.
After parsing the JSON response, the app presents all places in a RecyclerView, that loads 20 more places whenever the user scrolls to the end of the list (up to 60 places in total).
When a user clicks an item on the list, the app will open a new Map Activity that will show the place and its details on a Google Map using a drawbale resource marker.

2. MapActivity that presents the user's chosen burrito place with a red marker on a Google Map. Below the map there is a layout with a few more details about the place (i.e. address and price level).


* Please Note: The app was tested with a GenyMotion emulator (Samsung galaxy S8-8.0.0-API 26). Hence if there is a problem with finding user's location, please go to MainActivity, to the buildURL method and do the following:
1) Comment the two lines where variables lat and lng are defined
2) Uncomment the two lines where lat and lng variables are defined with hardcoded values.
