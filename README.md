# CustomizationButterKnifeZelezny
=======
自动生成包含R2.id的注释、符合Android代码风格的以m开头的驼峰命名field以外，还提供了一个可以自动为TextView和ImageView设置setXX的方法。

因为代码中使用多模块模式，到时ButterKnife生成的注释格式为
```
    @BindView(R.id.tag_flow)
    TagFlowLayout tagFlow;
```
需要额外修改R.id为R2.id，同时为了保证代码风格需要将tagFlow修改为mTagFlow。
这里我们基于ButterKnifeZelezny插件做了一些额外修改，除了可以自动生成包含R2.id的注释
和符合我们代码风格的以m开头的驼峰命名field以外，还提供了一个可以自动为TextView和ImageView
设置setXX的方法。b
