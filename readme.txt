
酷宅 IoT 项目合作流程草案(How to get cooperation with coolkit iot)

中文版

1. 初步接触阶段

客户联系上我们（通常海外客户会联系 ITEAD），说想做一个 xxx 智能项目，希望能够用我们的 xx 智能模块/设备、服务器及 App。问我们能够提供哪些资料，以便于他评估。

我们会提供：

Demo App（GitHub：https://github.com/CoolKit-Technologies/open-coolkit-android）
Android SDK 及文档（GitHub：https://github.com/CoolKit-Technologies/open-coolkit-android/tree/master/doc）
统一的 ITEAD Maker App ID/Secret（限制权限）
通过 Demo App，客户可以：

熟悉 Android App 架构及 SDK 用法
定制自己的 App UI
控制所有 ITEAD Maker 品牌设备（即从 ITEAD iMall 网站上购买的智能产品）
但是不可以（这些功能需要通过 eWeLink App 来完成）：

用户管理（新建用户、修改密码等等）
设备管理（添加设备、删除设备等等）
备注：处于初步接触阶段的客户，我们不提供任何形式的技术支持。但是如果客户在使用 Demo App 过程中发现了 BUG，可以提交到 GitHub 的 Issue，我们会抽空处理。

2. 注册成为合作伙伴阶段（Partnership Establishment）

第一阶段之后，客户有兴趣基于我们的智能硬件模块/产品开发他自己品牌的产品，并需要进一步评估我司 IoT 服务端接口。

我们需要客户提供他的相关资料：

注册邮箱、手机号、联系人姓名
公司信息：名称、地址、营业执照
品牌信息
型号信息
我们要收取客户 $99 USD 的费用。

我们会给客户提供 CoolKit IoT Starter Kit，包含：

服务端 API 文档及跟服务端交互的流程说明文档
专用的 App ID/Secret（有效期一年，权限无限制，除了注册新用户）
5 个智能产品（后台数据被修改成客户的品牌及型号）
客户可以：

将 Demo App 的 App ID/Secret 更新为他自己专用的 App ID/Secret
对这 5 个智能产品执行所有设备管理操作
使用其他开发语言开发任一平台的 App，比如 iOS App、Windows App、Web App
客户不可以：

用他自己开发的 App 注册新用户
备注：处于合作伙伴阶段的客户，我们提供邮件形式的技术支持。

3. 完成首笔付款，正式启动项目阶段

第二个阶段后，客户完成所有技术评估。然后客户跟我们确认项目细节，我们完成项目报价（硬件、License、可能的 App 定制），双方签署项目合同，客户完成首笔付款，项目正式启动。

我们会给客户提供：

按照合同提供硬件
增加 App ID/Secret 用户管理功能（注册新用户等等）
提供完善的技术支持（邮件、Skype）


Stage 1. Preliminary Evaluation

After we have a preliminary understanding of each side, we can provide below information for your evaluation :

Demo App source code（Could be download from GitHub）
Android SDK and related documents (Can also be downloaded from GitHub)
An APP ID is already included in Demo App but this APP ID has only limited privilege.
You could:

Modify the UI and business logic of Android Demo App, compile it, test it against our IoT platform, and control all products under ITEAD brand. 
You could not: 

Manage account (such as create new account), manage devices (such as add device). You will need eWeLink app to do all of this.
In one word, Demo App is intended to demonstrate how to control devices only
At this stage, we are also hoping you can get a better understanding of our Android SDK.

Note: we will not provide technical support at this stage.

Stage 2. Partnership Establishment

When we have discussed all the details of your project and reached an agreement on the process, cost, payment of the project, we will provide you:

Web API and API documents (you could use whatever programming language you prefer to access our IoT platform).
A partner account on our platform, you will need to provide us following information: manufacturer, brand, product model, product description, etc.
Specific App ID—— This App ID gives you full privilege to access our IoT platform (except register new user account), and enables you to control all products under your own brand. 
Development kit (5 real devices under your brand)
At this stage, We will charge you $99 USD. The Specific APP ID is valid for one year, and we will provide technical support by email . The APP ID enables you to create your own app to control all devices under your brand.

Stage 3. Place an order 

After the project gets your confirmation, and both side have reach an agreement on estimated cost on hardware, app, cloud server, then we’ll sign up a contract. Upon receiving payment for your first order, we will provide you:

An all-around technical support.
Non-limited APP ID.


项目向导
1:该项目在android studio中创建，建议使用android studio导入项目。
2:该项目中使用的appid为个人开发者角色，


通过 Demo App，客户可以：
熟悉 Android App 架构及 SDK 用法
定制自己的 App UI
控制所有 ITEAD Maker 品牌设备（即从 ITEAD iMall 网站上购买的智能产品,https://www.itead.cc/smart-home/sonoff-wifi-wireless-switch.html）

但是不可以（这些功能需要通过 eWeLink App 来完成）：
用户管理（新建用户、修改密码等等）
设备管理（添加设备、删除设备等等）

3.支持设备范围：
单通道开关改装件，多通道开关改装件

4:使用步骤
a.获取改装件(可联系客服购买,联系邮箱：jason.zhang@itead.cc)
b.在应用市场搜索ewelink并下载安装。
c.使用ewelink注册用户并加设备。
d.运行本项目，使用刚刚注册的账号登录。
e.在列表中可以看到改设备，点击设备进入详情可以控制开关设备,设置定时器。

5:查看doc目录下的sdk和说明文档以获得更多帮助
－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
project guid
1:this project is created under android studio，we suggest your use android studio to import this project
2.this project has a  AppID which is authorized as develop role ,which means:
capable:

get familiar with android structure and how to use SDK to create app.
custom App UI
control all devices which in brand  ITEAD and Maker （Which you purchased from ITEAD iMall,https://www.itead.cc/smart-home/sonoff-wifi-wireless-switch.html）

which is not capable:
user operations(for example, register, change password)
manage device (for example: add a device,delete a device).

3:devices which this project support
Sonoff - WiFi

4:steps
a.get a device(purchase from itead,contact email:jason.zhang@itead.cc)
b.search ewelink then install。
c.open ewelink, register with your phone  or email,add the device purchased form itead to your ewelink app account 。
d.run this open coolkit project and login with you ewelink account。
e.your can see the device under your account, control device  like turn on,turn off, or set a timer 。

5.check sdk doc under directory doc in this project for more help.








