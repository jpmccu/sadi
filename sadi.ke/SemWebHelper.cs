﻿using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using SemWeb;

namespace SADI.KEPlugin
{
    public class SemWebHelper
    {
        public static Store resolveURI(Uri uri)
        {
            Store store = new SemWeb.MemoryStore();
            store.Import(SemWeb.RdfReader.LoadFromUri(uri));
            return store;
        }

        public static string storeToString(Store store)
        {
            StringWriter buf = new StringWriter();
            try
            {
                using (RdfWriter writer = new N3Writer(buf))
                {
                    writer.Write(store);
                }
            }
            catch (Exception e)
            {
                buf.WriteLine(e.ToString());
            }
            return buf.ToString();
        }
    }
}
