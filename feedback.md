# Feedback

### Improvements that can be done to this project:
- Since this project started with a small scale in mind a simple MVVM architecture was used. If this project needs to be scaled to a very large scale then it would be better to go with something like clean architecture
- Some codebase (logic) can be abstracted to the viewmodel
- This project was going to only contain a very few activities and hence I didn't use the one activity multiple fragments architecture to avoid complications
- The calling and receiving call user interface can be improved
- Multiple users calling at the same time is not handled as of now
- The VideoChatActivity has a lot of scope for improvement in terms of functionality like displaying volume indicator, mute icon when the remote user has muted and handling video turning off of the remote user

### Observations about the Agora RTC and RTM SDKs:
- The documentation and the entire process of the RTC SDK was very nicely done and was easy to follow along
- RTM implementation was a little confusing from the docs. Had to look through multiple codebases to understand how and where to initialise it
- RTMCallManager also was somewhat confusing and had to spend some time experimenting on how to use it with RTM
- Implementing the RtmClientListener and RtmCallEventListener functions was cumbersome. Most of the functions would not be relevant in most projects.