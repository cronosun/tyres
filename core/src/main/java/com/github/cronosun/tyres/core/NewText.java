package com.github.cronosun.tyres.core;

public abstract class NewText implements NewRes<NewText> {

    private NewText() {}

    public static final NewText create(BundleInfo _ignored, ResInfo info) {
        return new NewTextNoArgs(info);
    }

    public abstract NewText withArgs(Object[] args);

    private static final class NewTextNoArgs extends NewText {
        private final ResInfo resInfo;

        public NewTextNoArgs(ResInfo resInfo) {
            this.resInfo = resInfo;
        }

        @Override
        public ResInfo info() {
            return resInfo;
        }

        @Override
        public NewText withArgs(Object[] args) {
            return new NewTextArgs(resInfo, args);
        }

        @Override
        public Object[] args() {
            // TODO: Return something static
            return new Object[]{};
        }
    }

    private static final class NewTextArgs extends NewText {
        private final ResInfo resInfo;
        public NewTextArgs(ResInfo resInfo, Object[] args) {
            this.resInfo = resInfo;
            this.args = args;
        }
        private final Object[] args;
        @Override
        public ResInfo info() {
            return resInfo;
        }
        @Override
        public NewText withArgs(Object[] args) {
            return new NewTextArgs(resInfo, args);
        }
        @Override
        public Object[] args() {
            return args;
        }
    }
}