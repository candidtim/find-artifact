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

To build a ring uberjar:

    lein clean && lein ring uberjar

Deployment is managed by Ansible. Create `hosts` file:

    [findartifactweb]
    ip or hostname

Configure deployment variables:

    cp vars.yml.template vars.yml
    # edit vars.yml

Deploy/Update application on the server:

    ansible-playbook -i hosts playbook.yml

## License

Copyright © 2015 Timur Rubeko
