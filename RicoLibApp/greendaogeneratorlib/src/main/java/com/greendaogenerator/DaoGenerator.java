package com.greendaogenerator;


import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGenerator {
    public static void main(String[] args) throws Exception {
/*
//        System.out.println(3 / 4);

//        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
//        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "gen");
//      当然，如果你愿意，你也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示：
//      Schema schema = new Schema(1, "me.itangqi.bean");
//      schema.setDefaultJavaPackageDao("me.itangqi.dao");

        // 模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections。
        // schema2.enableActiveEntitiesByDefault();
        // schema2.enableKeepSectionsByDefault();

        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
//        addCarProp(schema);
//        addCarPropCategory(schema);

        // 最后我们将使用 DAOGenerator 类的 generateAll() 方法自动生成代码，此处你需要根据自己的情况更改输出目录（既之前创建的 java-gen)。
        // 其实，输出目录的路径可以在 build.gradle 中设置，有兴趣的朋友可以自行搜索，这里就不再详解。
        new de.greenrobot.daogenerator.DaoGenerator().generateAll(schema, "/Users/Rico/androidWorkspace/chelaibao/chelaibao_user/user1204/greendaogeneratorlib/src/main/java/");*/

//        test(new File("/Users/Rico/workOuts/"));

    }
/*

    public static File parentToNew = new File("e:/new");//目标文件夹

    public static void test(File parent) {
        File[] files = parent.listFiles();//得到parent文件夹下的所有文件
        for (File file :
                files) {//遍历这些文件
            if (file.isDirectory()) {//如果文件是文件夹
                File newFolder = new File(parentToNew, file.getName());//在目标文件夹中建立一个
                                                                        // 新的文件夹对象（
                                                                        //e:/new＋当前遍历到的
                                                                        // 文件夹的名称比如:a
                                                                        // ）就是 e:/new/a
                newFolder.mkdir();//在存储中建立文件夹e:/new/a
                parentToNew = newFolder;//给全局变量附值这个新建的文件夹，用于文件拷贝时候用
                System.out.println("=====文件夹－" + newFolder.getAbsolutePath());//打印－ －
                test(file);//进入这个文件夹（文件夹a）
                parentToNew = parentToNew.getParentFile();//当自文件夹中的文件处理完之后，设置变量
                                                        // 回到上层目录，用于上层目录之后的遍历
            }else {
                //TODO 处理文件
                File newFile = new File(parentToNew, file.getName());//在目标文件夹中建立新文件然后操作
                System.out.println("--文件－" + newFile.getAbsolutePath());
                //...
            }
        }
    }*/


    public final static String MD5(String s) {

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @param schema
     */
    private static void addNote(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「gen.Note」（既类名）
        Entity note = schema.addEntity("Note");
        // 你也可以重新给表命名
        // note.setTableName("NODE");

        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        // 接下来你便可以设置表中的字段：
        note.addIdProperty().autoincrement();
        note.addStringProperty("text").notNull();
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        // For example, a property called “creationDate” will become a database column “CREATION_DATE”.
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }

    private static void addTalkMsg(Schema schema) {
        Entity talkMsg = schema.addEntity("TalkMsg");
        talkMsg.addStringProperty("MSGID").primaryKey();
        talkMsg.addStringProperty("IMEI").notNull();
        talkMsg.addStringProperty("SENDER_ID");
        talkMsg.addStringProperty("SENDER");
        talkMsg.addStringProperty("SENDER_AVATAR");
        talkMsg.addIntProperty("VOICE_LEN");
        talkMsg.addStringProperty("SEND_TIME");
        talkMsg.addStringProperty("userId");
        talkMsg.addStringProperty("localPath");
        talkMsg.addLongProperty("sendTimeMillions");
        talkMsg.addBooleanProperty("selfMsg");
        talkMsg.addStringProperty("selfName");
        talkMsg.addStringProperty("selfAvatar");
    }
    private static void addPushMsg(Schema schema) {
        Entity talkMsg = schema.addEntity("PushMsg");
        talkMsg.addStringProperty("Id").primaryKey();
        talkMsg.addStringProperty("Type");
        talkMsg.addStringProperty("Descript");
        talkMsg.addStringProperty("SenderId");
        talkMsg.addStringProperty("Sender");
        talkMsg.addStringProperty("SendTime");
        talkMsg.addLongProperty("sendTimeMillions");
        talkMsg.addStringProperty("userId");
        talkMsg.addBooleanProperty("unRead");
    }


    private static void addCarPropCategory(Schema schema){
        Entity carPropCat = schema.addEntity("CarPropCategory");
        carPropCat.addIdProperty().autoincrement();
        carPropCat.addStringProperty("name");
        carPropCat.addStringProperty("modelsMidId");

    }

    private static void addCarProp(Schema schema){
        Entity carPropCat = schema.addEntity("CarProp");
        carPropCat.addIdProperty().autoincrement();
        carPropCat.addStringProperty("name");
        carPropCat.addStringProperty("value");
        carPropCat.addLongProperty("categoryId");

    }

}
