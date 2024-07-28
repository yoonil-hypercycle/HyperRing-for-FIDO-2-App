# HyperRing Core Library

## Introduction
The HyperRing Core Library provides a robust and flexible solution for integrating NFC and MFA capabilities into your applications. This guide will walk you through the steps to set up and use the HyperRing Core Library effectively.

## Installation

### Step 1: Configure your project
Add the following lines to the `dependencyResolutionManagement` section of your root `build.gradle` file to include the necessary repositories:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the library dependency
Include the HyperRing Core Library in your module's `build.gradle` file:

```groovy
dependencies {
    implementation 'com.github.HyperRingSW:HyperRingSDKCore:TAG'
}
```

Replace `TAG` with the specific version tag you want to use.

## Using HyperRingNFC

### Initializing HyperRingNFC
Before using any NFC-related functions, initialize the HyperRingNFC component:

```kotlin
HyperRingNFC.initializeHyperRingNFC(context)
```

### Starting NFC Tag Polling
To begin scanning for NFC tags, call the `startNFCTagPolling` function. This function requires the current activity context and a callback for when a tag is discovered:

```kotlin
HyperRingNFC.startNFCTagPolling(
    context as Activity, 
    onDiscovered = ::onDiscovered
)
```

### Stopping NFC Tag Polling
To stop scanning for NFC tags, use the `stopNFCTagPolling` function:

```kotlin
HyperRingNFC.stopNFCTagPolling(context as Activity)
```

### Handling Discovered Tags
The `onDiscovered` callback provides the `HyperRingTag` data when an NFC tag is found. This is where you can implement your logic for reading or writing to the tag:

```kotlin
private fun onDiscovered(hyperRingTag: HyperRingTag): HyperRingTag {
    // Your code to handle the discovered tag
}
```

### Reading and Writing Data
Use the `HyperRingNFC.write` and `HyperRingNFC.read` functions within the `onDiscovered` callback. The `HyperRingData` class is used for data encryption and decryption.

#### Writing Example 1: AES Encryption
The following example shows how to write data to an NFC tag using AES encryption:

```kotlin
HyperRingNFC.write(
    uiState.value.targetWriteId, 
    hyperRingTag, 
    AESHRData.createData(uiState.value.dataTagId ?: 10, "Jenny Doe")
)
```

#### Writing Example 2: JWT Encryption
The following example shows how to write data to an NFC tag using JWT:

```kotlin
HyperRingNFC.write(
    uiState.value.targetWriteId, 
    hyperRingTag, 
    JWTHRData.createData(10, "John Doe", MainActivity.jwtKey)
)
```

#### Reading Example
To read data from an NFC tag, use the following code:

```kotlin
val readTag: HyperRingTag? = HyperRingNFC.read(uiState.value.targetReadId, hyperRingTag)
```

## Using HyperRingMFA

### Initializing HyperRingMFA
Initialize the HyperRingMFA component with your MFA data. Ensure the `mfaData` parameter is not empty:

```kotlin
val mfaData: MutableList<HyperRingMFAChallengeInterface> = mutableListOf()
// AES Type
mfaData.add(AESMFAChallengeData(10, "dIW6SbrLx+dfb2ckLIMwDOScxw/4RggwXMPnrFSZikA=", null))
// JWT Type
mfaData.add(JWTMFAChallengeData(15, "John Doe", null, MainActivity.jwtKey))

HyperRingMFA.initializeHyperRingMFA(mfaData = mfaData.toList())
```

### Requesting MFA Authentication
To initiate MFA authentication, use the `requestHyperRingMFAAuthentication` function. This function requires the current activity context, a callback for when an NFC tag is discovered, and an option to auto-dismiss the dialog:

```kotlin
HyperRingMFA.requestHyperRingMFAAuthentication(
    activity = MainActivity.mainActivity!!,
    onNFCDiscovered = ::onDiscovered,
    autoDismiss = autoDismiss
)
```

### Handling MFA Authentication Response
The `onDiscovered` callback will receive the MFA challenge response. Use this response to verify the MFA authentication:

```kotlin
fun onDiscovered(dialog: Dialog?, response: MFAChallengeResponse?) {
    HyperRingMFA.verifyHyperRingMFAAuthentication(response)
}
```

This guide provides a detailed overview of how to set up and use the HyperRing Core Library in your applications. For more advanced usage and examples, please refer to the official documentation.