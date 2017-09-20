# Overview

This repository contains a starter for selling digital goods via [ethPay](https://ethpay.world). 
It illustrates how to configure *ethPay instant access* to give customers instant access to their purchase.


## Sample

Hava a look at the [sample](sample) Subdirectory which contains a *Dockerfile* to quickly set up the starter.
Copy some media files into the *files* directory and build the Docker image. 

Once the container is started you need to configure your store to use it as instant access endpoint. 
Read [here](https://pacsug.freshdesk.com/support/solutions/articles/14000071220) how this is done easily.

Afterwards you are ready to sell the media files and let the customer directly download them just by providing them an URI like this:


https://eth.de.com/0x2b2d1F98206d199F23C5C51fB1dEDBd8D543Aabf/purchase/11?message=Pretzel+Picture&amount=4.50&currency=EUR&data=pretzel.jpg

 + __https://eth.de.com__ the URI prefix which supports the redirection to a website 
 if it is not opened from within a mobile device or if no suitable wallet is installed
 + __0x2b2d1F98206d199F23C5C51fB1dEDBd8D543Aabf__ the store address which will receive the payment of this purchase
 + __11__ an arbitrary number which is stored *publicly on the blockchain* alongside the purchase
 + __Pretzel+Picture__ the product title which is shown to the customer
 + __4.50__ and __EUR__ the price which should be payed for the product
 + __pretzel.jpg__ the name of the file you put in the *files* directory





## How does it work?

When a merchant enables instant access, he needs to provide an *endpoint*. 
This endpoint will be opened by the customer to access the purchase.
Everytime the customer wants to access his purchase, the Wallet application adds a parameter to the endpoint which can be used 
 to authenticate the customer.
 
 Example: 
 
 https://theconfiguredendpoint.xxx?ethpay=0xabcd-Qmxxx-1024.FFFFFF
 
 + __theconfiguredendpoint.xxx__ the endpoint which was configured
 + __ethpay__ the HTTP GET parameter name which is added to the endpoint
 + __0xabcd__ the store address of the corresponding purchase
 + __Qmxxx__ the purchase receipt. The purchase receipt is unique for every purchase
 + __1024__ the timestamp of the request
 + __FFFFFF__ the signature of the previous parts (0xabcd-Qmxxx-1024) and used to authenticate the customer

Have a look [here](src/main/java/de/pacs/ethpay/digitalgoods/controller/AuthenticationController.java) to see how the signature can be verified.




