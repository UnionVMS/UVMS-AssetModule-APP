# AssetModule

####Module description/purpose

Asset module is supposed to be a generic module. Focuses on a general concept, which means the module should provide the basic data structures and business logic where other modules can be implemented. Modules implementing the Asset module, the generalization module, will result in having the same design and structure of implementation. This implies a better and easier way for maintenance, but also a more efficient way to build new modules. 
But because of incomplete requirements, undefined stakeholders and incomplete usage area when design and implementation phase started for Asset module, has led to the module has a design and implementation that support specifically for a fishing vessel.
 
The main functionalities of the Asset module are store and provide information of a vessel to other integrated modules. The vessel information can be accessed and stored in two ways, either by calling the REST-API interface or by a message queue service. Please see the Java doc for more information. 
