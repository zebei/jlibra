# jlibra
A Java library for interacting with the Libra blockchain 

![Overview](docs/img/jlibra.png)

*Note: Due to breaking changes in the Libra testnet, there might be errors due to old protobuf files. This remark will be removed when the new files are committed.* https://community.libra.org/t/testnet-update-scheduled-for-july-10th-with-breaking-changes/1143

# Prerequisites

* JDK 8+
* Maven 3+

# Setup

* Clone this repo
* Build project with `mvn install`

# Usage

Start sample Main classes in `dev.jlibra.example` package for `Transfer`, `GetAccountState` and `KeyGeneration` examples.

# How-Tos & Step-by-Step Guides

How-Tos and Step-by-Step Guides are gathered in a [separate document](docs/HOWTO.md).

# Known Issues

**Transaction is not executed, but without errors (no events, no transaction in librabrowser.io)**

The execution of the example main classes might terminate before the actual action is performed. 
To prevent this, add `Thread.sleep(5000)` after the last statement of the example.