/*
 * -------------------------------------------------------------------------------------
 * Mi-Me Confidential
 *
 * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 * All rights reserved.
 *
 * No part of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior
 * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 * -------------------------------------------------------------------------------------
 */
package com.heling.utils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * <<description>>
 * Version     : 1.0
 * Created By  : nicholas
 * Created Date: 2016/4/3
 */
public class ServletInputStreamCopier extends ServletInputStream {
    private ByteArrayInputStream bais;

    public ServletInputStreamCopier(byte[] in) {
        this.bais = new ByteArrayInputStream(in);
    }

    @Override
    public boolean isFinished() {
        return bais.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int read() throws IOException {
        return this.bais.read();
    }
}
