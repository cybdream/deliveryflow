# Local Account Setup

The application can create one local administrator and one local driver when it starts with the `local` profile.

1. Open `src/main/resources/application-local.properties`.
2. Set strong, personal values for `app.bootstrap.admin-password` and `app.bootstrap.driver-password`.
3. Restart the application.
4. Confirm that the `users` table contains the two accounts.

The passwords are stored only as BCrypt hashes. `application-local.properties` is excluded from Git, so passwords must never be added to committed files.

## Development accounts

| Role | Default email | Password |
|---|---|---|
| Administrator | `admin@deliveryflow.local` | Set locally by the developer |
| Driver | `driver@deliveryflow.local` | Set locally by the developer |

Accounts are created only if no account already has the same email. Restarting the application will not overwrite an existing password.
