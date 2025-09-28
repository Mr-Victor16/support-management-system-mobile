# Support Management System [Mobile app]  
An Android mobile application that serves as a client for the backend system for managing technical support tickets. It allows users, operators and administrators to fully interact with the system from a mobile device.  
  
The application requires a running backend server to function correctly. The backend repository can be found here: [support_management_system-backend](https://github.com/Mr-Victor16/support_management_system).  
  
## Technology stack  
- Programming language: Java  
- Architecture: MVVM (Model-View-ViewModel)  
- Android SDK: Core components and libraries of the Android platform.   
- Retrofit: An HTTP client for efficient and clean communication with the REST API.   
- Gson: A library for serializing and deserializing Java objects to JSON.   
- JWT (JSON Web Token): Handles authorization and authentication for the user session.   
- Hilt: A library for dependency injection to maintain clean code.   
- Glide: An advanced library for loading, caching, and displaying images.  
  
## Features
The application adjusts views and permissions based on the logged-in user's role (User, Operator, Administrator).

### Unauthenticated User
- Browsing the knowledge base and the list of supported software.
- Registering for a new account and logging in.

### Authenticated User
- Editing their own user profile.
- Creating and editing their own support tickets.
- Adding replies to their own tickets.
- Viewing a list of their own tickets.

### Operator
- Managing all user tickets and their replies.
- Managing user accounts.
- Viewing system configuration lists (categories, knowledge base, priorities, software and statuses).

### Administrator
- Full management (Create, Read, Update, Delete) of users, categories, knowledge base, priorities, software and statuses.

## Screenshots
<kbd>![register_form](https://github.com/user-attachments/assets/d56856ce-603b-4df5-a9aa-ee7ad07dcaf0)</kbd>  
_Registration form._  <br /><br />

<kbd>![login_form](https://github.com/user-attachments/assets/b5752ac2-1a80-443a-821c-ee94035e0cb3)</kbd>  
_Login form._  <br /><br />

<kbd>![welcome_screen](https://github.com/user-attachments/assets/9d7c30e4-5b62-4ec3-9b94-d559556e9fb2)</kbd>  
_Welcome screen._  <br /><br />

<kbd>![knowledge_base](https://github.com/user-attachments/assets/181e4093-ae25-4ae5-a449-69ddf1894042)</kbd>  
_Knowledge base._  <br /><br />

<kbd>![ticket_details](https://github.com/user-attachments/assets/cbf1d342-ed59-4f37-ae9f-00757afcbcff)</kbd>  
_Preview ticket details._  <br /><br />

<kbd>![ticket_image](https://github.com/user-attachments/assets/54219961-4959-42fc-9f3e-351b4d6e4002)</kbd>  
_Preview of the ticket images._  <br /><br />
  
<kbd>![management_panel](https://github.com/user-attachments/assets/63231b09-83b6-4c7b-8d86-fdb396536286)</kbd>  
_Main view of the management panel._  <br /><br />

<kbd>![edit_user_validation](https://github.com/user-attachments/assets/7280f983-67b2-4903-8e6c-8b20628aa21d)</kbd>  
_User edit form with validation error._  <br /><br />

<kbd>![edit_status](https://github.com/user-attachments/assets/c50dbe0f-fcd9-4f36-9341-1378e2c69507)</kbd>  
_Dialog box for editing the status._  
