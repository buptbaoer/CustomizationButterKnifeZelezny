package com.avast.android.butterknifezelezny;

import com.avast.android.butterknifezelezny.setting.FormatSetting;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.CollectionListModel;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class HolderMethodInjectionAction extends AnAction {

    private FormatSetting formatSetting;

    public HolderMethodInjectionAction() {
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        this.formatSetting = FormatSetting.getInstance();
        this.generateGSMethod(this.getPsiMethodFromContext(e));
    }

    private void generateGSMethod(final PsiClass psiMethod) {
        (new WriteCommandAction.Simple(psiMethod.getProject(), new PsiFile[]{psiMethod.getContainingFile()}) {
            @Override
            protected void run() throws Throwable {
                HolderMethodInjectionAction.this.createGetSet(psiMethod);
            }
        }).execute();
    }

    private void createGetSet(PsiClass psiClass) {
        List fields = (new CollectionListModel(psiClass.getFields())).getItems();
        if (fields != null) {
            List list = (new CollectionListModel(psiClass.getMethods())).getItems();
            HashSet methodSet = new HashSet();
            Iterator elementFactory = list.iterator();

            while (elementFactory.hasNext()) {
                PsiMethod m = (PsiMethod) elementFactory.next();
                methodSet.add(m.getName());
            }
            HashSet fieldsSet = new HashSet();
            Iterator elementFeildFactory = fields.iterator();
            while (elementFeildFactory.hasNext()) {
                PsiField m = (PsiField) elementFeildFactory.next();
                fieldsSet.add(m.getName());
            }

            PsiElementFactory elementFactory1 = JavaPsiFacade.getElementFactory(psiClass.getProject());
            Iterator m1 = fields.iterator();

            while (m1.hasNext()) {
                PsiField field = (PsiField) m1.next();
                if (field.getType().getPresentableText().contains("TextView") || field.getType().getPresentableText().contains("ImageView")) {
                    if (!field.getModifierList().hasModifierProperty("final")) {
                        String methodText = this.buildSet(field);
                        elementFactory1 = JavaPsiFacade.getElementFactory(psiClass.getProject());
                        PsiMethod toMethod = elementFactory1.createMethodFromText(methodText, psiClass);
                        if (!methodSet.contains(toMethod.getName())) {
                            psiClass.add(toMethod);
                        }
                    }
                }
            }

            List interfaceSList = (new CollectionListModel(psiClass.getAllInnerClasses())).getItems();
            PsiClass interF = elementFactory1.createInterface("IEventListener");
            HashSet interfaceSet = new HashSet();
            Iterator interfaceSetFactory = interfaceSList.iterator();

            while (interfaceSetFactory.hasNext()) {
                PsiClass m = (PsiClass) interfaceSetFactory.next();
                interfaceSet.add(m.getName());
            }
            PsiField mIEventListener = elementFactory1.createFieldFromText("private IEventListener mIEventListener;\n", psiClass);
            if (!fieldsSet.contains(mIEventListener.getName())) {
                psiClass.add(mIEventListener);
            }
            String methodText = this.buildSetLinster(psiClass);
            PsiMethod toMethod = elementFactory1.createMethodFromText(methodText, psiClass);
            if (!methodSet.contains(toMethod.getName())) {
                psiClass.add(toMethod);
            }
            if (!interfaceSet.contains(interF.getName())) {
                psiClass.add(interF);
            }
            CodeStyleManager.getInstance(psiClass.getProject()).reformat(psiClass);
        }
    }

    private String buildSetLinster(PsiClass field) {
        StringBuilder sb = new StringBuilder();
        sb.append("public ");
        sb.append(field.getName() + " ");
        sb.append(" setOnEventListener(IEventListener mOnEventListener) {\n" +
                "        this.mIEventListener = mOnEventListener;\n" +
                "        return this;\n" +
                "    }");

        return sb.toString();
    }

    private String buildSet(PsiField field) {
        StringBuilder sb = new StringBuilder();

        sb.append("public ");
        if (field.getModifierList().hasModifierProperty("static")) {
            sb.append("static ");
        }

        sb.append(field.getContainingClass().getName() + " ");
        sb.append("set" + this.getFirstUpperCase(field.getName()));

        if (field.getType().getPresentableText().contains("TextView")) {
            sb.append("(String str" + "){\n");
            sb.append("this." + field.getName() + ".setText(str);\n");
        } else if (field.getType().getPresentableText().contains("ImageView")) {
            sb.append("(String imageUrl" + "){\n");
            sb.append(" Glide.with(ResUtil.getContext())\n" +
                    "                .load(imgurl)\n" +
                    "                .apply(new RequestOptions()\n" +
                    "                .fallback(R.drawable.common_default_portrait)\n" +
                    "                .placeholder(R.drawable.common_default_portrait)\n" +
                    "                .dontAnimate()\n" +
                    "                )\n" +
                    "                .into(" +
                    field.getName() + ");\n");
        } else {
            sb.append("(String str" + "){\n");
        }

        sb.append("return  this;\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String getFirstUpperCase(String oldStr) {
        if (oldStr != null && oldStr.length() > 1) {
            String secondChar = oldStr.substring(1, 2);
            if (oldStr.startsWith("m") && secondChar.equals(secondChar.toUpperCase())) {
                return oldStr.substring(1);
            } else {
                return oldStr.substring(0, 1).toUpperCase() + oldStr.substring(1);
            }
        }
        return oldStr;
    }

    private PsiClass getPsiMethodFromContext(AnActionEvent e) {
        PsiElement elementAt = this.getPsiElement(e);
        return elementAt == null ? null : (PsiClass) PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = (PsiFile) e.getData(LangDataKeys.PSI_FILE);
        Editor editor = (Editor) e.getData(PlatformDataKeys.EDITOR);
        if (psiFile != null && editor != null) {
            int offset = editor.getCaretModel().getOffset();
            return psiFile.findElementAt(offset);
        } else {
            e.getPresentation().setEnabled(false);
            return null;
        }
    }

    private String format(String string, PsiField field,String paramsName) {
        String oldContent;
        if (field.getDocComment() == null) {
            oldContent = field.getText().substring(0, field.getText().lastIndexOf("\n") + 1);
        } else {
            oldContent = field.getDocComment().getText();
        }

        oldContent = oldContent.substring(0, oldContent.length()).replaceAll("[\n,\r,*,/,\t]", "").trim();
        if ("set".equals(string)) {
            oldContent = this.formatSetting.getSetFormat().replaceAll("#\\{bare_field_comment}", "").replaceAll("\\$\\{field.name}", paramsName);
        }

        return oldContent;
    }
}
