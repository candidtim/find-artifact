# Find Artifact

Find Artifact is a simple Maven Repository search tool. It's goal is best explained in how it differs from existing ones:

- it has extremely simple and bloat-free user interface
- it allows searching over multiple repositories at once (Maven Cnetral, Sonatype, etc.) *
- it shows always up-to-date information

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## Deployment

### Server requirements

Server should have nginx installed and configured. Nginx configuration is exepected in
`/usr/local/nginx/conf/nginx.conf`, and is expected to have a line `#insertserversbelow` somewhere in the `http`
section, after which the server configuration will be inserted.

Server should have an active user which will run the application. User should be present before plyabook, as it
will not create one otherwise.

### Building an app for deployment

To build a ring uberjar:

    lein clean && lein ring uberjar

### Ansible config and use

Create `hosts` file:

    [findartifactweb]
    ip or hostname

Configure deployment variables:

    cp vars.yml.template vars.yml
    # edit vars.yml

Deploy/Update application on the server:

    ansible-playbook -i hosts playbook.yml

## License

Copyright © 2015 Timur Rubeko
