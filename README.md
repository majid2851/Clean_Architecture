Clean Architecture

Android Note Apk with Clean Architecture by layer.

Clean architecture was proposed by Robert C. Martin in 2012 in the Clean Code Blog and it follow the SOLID principle.

![clean_arch](https://user-images.githubusercontent.com/46685643/231424519-a71193c8-87a3-4dcb-9768-8855883f1028.jpeg)


The circles represent different layers of your app. Note that:

The center circle is the most abstract, and the outer circle is the most concrete. This is called the Abstraction Principle. The Abstraction Principle specifies that inner circles should contain business logic, and outer circles should contain implementation details.

Another principle of Clean Architecture is the Dependency Inversion. This rule specifies that each circle can depend only on the nearest inward circle ie. low-level modules do not depend on high-level modules but the other way around.


![68747470733a2f2f636f64696e67776974686d697463682e73332e616d617a6f6e6177732e636f6d2f7374617469632f636f75727365732f32312f636c65616e5f6172636869746563747572655f6469616772616d7](https://user-images.githubusercontent.com/46685643/231425227-d1f8726f-7ce5-4622-b0ad-342256c83f01.png)











